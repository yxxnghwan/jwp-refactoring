package kitchenpos.application;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.application.dto.CreateMenuDto;
import kitchenpos.application.dto.MenuDto;
import kitchenpos.domain.menu.Menu;
import kitchenpos.domain.menu.MenuGroupRepository;
import kitchenpos.domain.menu.MenuProduct;
import kitchenpos.domain.menu.MenuRepository;
import kitchenpos.domain.product.Product;
import kitchenpos.domain.product.ProductRepository;
import kitchenpos.domain.service.CalculateProductPriceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final ProductRepository productRepository;
    private final CalculateProductPriceService calculateProductPriceService;

    public MenuService(final MenuRepository menuRepository,
                       final MenuGroupRepository menuGroupRepository,
                       final ProductRepository productRepository,
                       final CalculateProductPriceService calculateProductPriceService) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.productRepository = productRepository;
        this.calculateProductPriceService = calculateProductPriceService;
    }

    @Transactional
    public MenuDto create(final CreateMenuDto createMenuDto) {
        validateExistMenuGroup(createMenuDto);
        final Menu menu = mapToMenu(createMenuDto);
        menu.validateOverPrice(calculateProductPriceService);
        final Menu savedMenu = menuRepository.save(menu);
        return MenuDto.of(savedMenu);
    }

    private void validateExistMenuGroup(final CreateMenuDto createMenuDto) {
        if (!menuGroupRepository.existsById(createMenuDto.getMenuGroupId())) {
            throw new IllegalArgumentException();
        }
    }

    private Menu mapToMenu(final CreateMenuDto createMenuDto) {
        final List<MenuProduct> menuProducts = extractToMenuProduct(createMenuDto);
        return Menu.create(
                createMenuDto.getName(),
                createMenuDto.getPrice(),
                menuGroupRepository.findById(createMenuDto.getMenuGroupId()).orElseThrow(),
                menuProducts
        );
    }

    private List<MenuProduct> extractToMenuProduct(final CreateMenuDto createMenuDto) {
        final List<Long> productIds = createMenuDto.getProductIds();
        final List<Product> products = productRepository.findAllByIds(productIds);
        return createMenuDto.getMenuProducts()
                .stream()
                .map(createMenuProductDto -> {
                    final Product product = findProductById(products, createMenuProductDto.getProductId());
                    return new MenuProduct(
                            product.getName(),
                            product.getPriceValue(),
                            createMenuProductDto.getProductId(),
                            createMenuProductDto.getQuantity()
                    );
                })
                .collect(Collectors.toList());
    }

    private Product findProductById(final List<Product> products, final Long productId) {
        return products.stream()
                .filter(product -> product.getId().equals(productId))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public List<MenuDto> list() {
        final List<Menu> menus = menuRepository.findAll();

        return menus.stream()
                .map(MenuDto::of)
                .collect(Collectors.toList());
    }
}
