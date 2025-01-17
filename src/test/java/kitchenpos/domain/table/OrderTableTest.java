package kitchenpos.domain.table;

import static kitchenpos.domain.DomainTestFixture.getTestOrderTable1;
import static kitchenpos.domain.DomainTestFixture.getTestOrderTable2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.domain.service.FindOrderTableInOrderStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTableTest {

    @Test
    @DisplayName("기본 테이블을 생성한다.")
    void create() {
        final OrderTable orderTable = OrderTable.create();

        assertAll(
                () -> assertThat(orderTable.getTableGroup()).isNull(),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("테이블이 그룹에 속했는지 확인한다.")
    void isGrouped() {
        final OrderTable testOrderTable1 = getTestOrderTable1();
        final OrderTable testOrderTable2 = getTestOrderTable2();
        testOrderTable1.joinTableGroup(
                new TableGroup(
                        1L,
                        LocalDateTime.now(),
                        List.of(testOrderTable1, testOrderTable2)
                )
        );
        final OrderTable testOrderTable3 = getTestOrderTable2();
        testOrderTable3.ungroup();

        assertAll(
                () -> assertThat(testOrderTable1.isGrouped()).isTrue(),
                () -> assertThat(testOrderTable3.isGrouped()).isFalse()
        );
    }

    @Test
    @DisplayName("테이블의 채움 상태를 변경한다.")
    void changeEmpty() {
        final OrderTable testOrderTable1 = getTestOrderTable1();
        testOrderTable1.changeEmpty(false);

        assertThat(testOrderTable1.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("테이블의 손님 수를 세팅한다.")
    void enterGuests() {
        final OrderTable testOrderTable1 = getTestOrderTable1();
        testOrderTable1.changeEmpty(false);
        testOrderTable1.enterGuests(95);

        assertThat(testOrderTable1.getNumberOfGuests()).isEqualTo(95);
    }

    @Test
    @DisplayName("테이블을 테이블 그룹에 소속시킨다.")
    void joinTableGroup() {
        final OrderTable testOrderTable1 = getTestOrderTable1();
        final OrderTable testOrderTable2 = getTestOrderTable2();
        testOrderTable1.joinTableGroup(
                new TableGroup(
                        1L,
                        LocalDateTime.now(),
                        List.of(testOrderTable1, testOrderTable2)
                )
        );

        assertThat(testOrderTable1.getTableGroupId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("테이블을 그룹에서 해제시킨다.")
    void ungroup() {
        final OrderTable testOrderTable1 = getTestOrderTable1();
        final OrderTable testOrderTable2 = getTestOrderTable2();
        testOrderTable1.joinTableGroup(
                new TableGroup(
                        1L,
                        LocalDateTime.now(),
                        List.of(testOrderTable1, testOrderTable2)
                )
        );

        testOrderTable1.ungroup();

        assertThat(testOrderTable1.getTableGroup()).isNull();
    }

    @Test
    @DisplayName("테이블의 상태가 비울 수 있는 상태인지 확인한다.")
    void validateEmptyAvailable() {
        final OrderTable testOrderTable1 = getTestOrderTable1();
        final FindOrderTableInOrderStatusService findOrderTableInOrderStatusService
                = mock(FindOrderTableInOrderStatusService.class);
        when(findOrderTableInOrderStatusService.existByOrderStatus(any(), any())).thenReturn(false);

        assertDoesNotThrow(() -> testOrderTable1.validateEmptyAvailable(findOrderTableInOrderStatusService));
    }

    @Test
    @DisplayName("테이블의 상태가 비울 수 없는 상태면 예외가 발생한다.")
    void validateEmptyNotAvailable() {
        final OrderTable testOrderTable1 = getTestOrderTable1();
        final FindOrderTableInOrderStatusService findOrderTableInOrderStatusService
                = mock(FindOrderTableInOrderStatusService.class);
        when(findOrderTableInOrderStatusService.existByOrderStatus(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> testOrderTable1.validateEmptyAvailable(findOrderTableInOrderStatusService))
                .isInstanceOf(IllegalArgumentException.class);
    }
}