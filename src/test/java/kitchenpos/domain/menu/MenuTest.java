package kitchenpos.domain.menu;

import static kitchenpos.domain.DomainTestFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import kitchenpos.domain.DomainTestFixture;
import kitchenpos.domain.service.CalculateProductPriceService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MenuTest {

    @Test
    @DisplayName("메뉴를 생성한다.")
    void create() {
        final Menu menu = Menu.create(
                "테스트 메뉴",
                BigDecimal.valueOf(1000L),
                testMenuGroup.getId(),
                List.of(testMenuProduct1, testMenuProduct2)
        );

        assertAll(
                () -> assertThat(menu.getName()).isEqualTo("테스트 메뉴"),
                () -> assertThat(menu.getMenuGroupId()).isEqualTo(testMenuGroup.getId()),
                () -> assertThat(menu.getMenuProducts()).containsExactly(testMenuProduct1, testMenuProduct2),
                () -> assertThat(menu.getPrice().longValue()).isEqualTo(1000L)
        );
    }

    @Test
    @DisplayName("menu의 가격이 menuProduct의 가격 전체 합보다 낮은지 검사한다.")
    void validateOverPrice() {
        final CalculateProductPriceService calculateProductPriceService = mock(CalculateProductPriceService.class);
        final Menu menu = Menu.create(
                "테스트 메뉴",
                BigDecimal.valueOf(1000L),
                testMenuGroup.getId(),
                List.of(testMenuProduct1, testMenuProduct2)
        );
        when(calculateProductPriceService.calculateMenuProductPriceSum(anyList()))
                .thenReturn(BigDecimal.valueOf(1001L));

        assertDoesNotThrow(() -> menu.validateOverPrice(calculateProductPriceService));
    }

    @Test
    @DisplayName("menu의 가격이 menuProduct의 가격 전체 합보다 높으면 예외가 발생한다.")
    void validateOverPriceSoExpensiveException() {
        final CalculateProductPriceService calculateProductPriceService = mock(CalculateProductPriceService.class);
        final Menu menu = Menu.create(
                "테스트 메뉴",
                BigDecimal.valueOf(1000L),
                testMenuGroup.getId(),
                List.of(testMenuProduct1, testMenuProduct2)
        );
        when(calculateProductPriceService.calculateMenuProductPriceSum(anyList()))
                .thenReturn(BigDecimal.valueOf(999L));

        assertThatThrownBy(() -> menu.validateOverPrice(calculateProductPriceService))
                .isInstanceOf(IllegalArgumentException.class);
    }
}