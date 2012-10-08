/*
 * Copyright (c) 2012 by James E. Scarborough.
 *
 * This file is part of Software Testing 123 examples.
 *
 * Software Testing 123 examples are free software: you can
 * redistribute them and/or modify them under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Software Testing 123 examples are distributed in the hope that
 * they will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Software Testing 123.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package example0001;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * This class demonstrates methods of isolating code for unit testing.
 * In various cases, there may be reasons to not have accessors or setters to
 * various classes your class under test depends upon, and you would like
 * a workaround.  This class demonstrates two.  This class also
 * demonstrates using EasyMock to create mock objects and verify
 * that they were used as expected.
 *
 * Typically, a test would be more thorough, but this example is limited
 * to showing methods of access.
 */
public class TuxMartTest {

    private Order order;
    private InventoryService mockInventoryService;
    private PaymentService mockPaymentService;
    private FinancialService mockFinancialService;

    /**
     * Before each test is run, we'll initialize the services and a mock order.
     *
     * If this had been an actual test of actual code, this kind of setup may
     * not have been in a @Before method, but might have been in a separate
     * metod for the test.
     */
    @Before
    public void setUp() {
        // Create a mock order
        order = EasyMock.createMock(Order.class);
        expect(order.getGrandTotal()).andStubReturn(new BigDecimal(42));
        expect(order.getLineItems()).andStubReturn(Arrays.asList(
                makeLineItem("La", "A note to follow \"So\"", new BigDecimal(4), new BigDecimal(5)),
                makeLineItem("Ti", "A drink with jam and bread", new BigDecimal(2), new BigDecimal(11))
        ));
        replay(order);

        // Create mock services
        mockPaymentService = EasyMock.createMock(PaymentService.class);
        mockInventoryService = EasyMock.createMock(InventoryService.class);
        mockFinancialService = EasyMock.createMock(FinancialService.class);

        // Set up those mock services to expect and return certain values
        expect(mockPaymentService.securePayment(new BigDecimal(42))).andStubReturn(true);
        for (LineItem li : order.getLineItems()) {
            expect(mockInventoryService.deductStock(li.getProduct(), li.getQuantity())).andStubReturn(BigDecimal.ZERO);
        }
        mockFinancialService.createOrder(order);

        // Switch the mock services into "replay" mode from the initial "record" mode
        replay(mockPaymentService);
        replay(mockInventoryService);
        replay(mockFinancialService);
    }

    /**
     * This test provides mock services to the class under test
     * by overriding getters in an anonymous inner class.
     */
    @Test
    public void testMakeSale_byOverriding() {

        TuxMart tm = new TuxMart() {
            @Override
            protected PaymentService getPaymentService() {
                return mockPaymentService;
            }

            @Override
            protected InventoryService getInventoryService() {
                return mockInventoryService;
            }

            @Override
            protected FinancialService getFinancialService() {
                return mockFinancialService;
            }
        };

        tm.makeSale(order);
        verify(mockPaymentService, mockInventoryService, mockFinancialService);
    }

    /**
     * This test injects mock services to the class under test using a
     * custom injection method {@link #inject(Object, Object)}.
     *
     * @throws IllegalAccessException
     */
    @Test
    public void testMakeSale_byReflection() throws IllegalAccessException {
        TuxMart tm = new TuxMart();
        inject(tm, mockPaymentService);
        inject(tm, mockInventoryService);
        inject(tm, mockFinancialService);

        tm.makeSale(order);
        verify(mockPaymentService, mockInventoryService, mockFinancialService);
    }

    /**
     * Inject a value to a private field on the target class.  This implementation
     * determines the appropriate field by the field's type and the type of the object
     * to inject, and stops at the first assignable field.
     *
     * @param target  This is the object whose field will be injected
     * @param toInject  This is the item to inject
     * @throws IllegalAccessException
     */
    private void inject(Object target, Object toInject) throws IllegalAccessException {
        Class<?> targetClass=target.getClass();
        for (Field f:targetClass.getDeclaredFields()) {
            if (f.getType().isAssignableFrom(toInject.getClass())) {
                f.setAccessible(true);
                f.set(target,toInject);
                return;
            }
        }
        throw new IllegalStateException("No compatible fields found.");
    }

    /**
     * This method creates a stub line item with an attendant mock product.
     *
     * @param sku the key for the "stock keeping unit"
     * @param description A description of the product
     * @param unitPrice The unit cost of the product
     * @param quantity The quantity of product purchased
     * @return A stub line item
     */
    private LineItem makeLineItem(String sku, String description, BigDecimal unitPrice, BigDecimal quantity) {
        final LineItem line = EasyMock.createMock(LineItem.class);
        final Product product = EasyMock.createMock(Product.class);
        expect(product.getDescription()).andStubReturn(description);
        expect(product.getSku()).andStubReturn(sku);
        replay(product);
        expect(line.getProduct()).andStubReturn(product);
        expect(line.getQuantity()).andStubReturn(quantity);
        expect(line.getUnitPrice()).andStubReturn(unitPrice);
        expect(line.getExtendedPrice()).andStubReturn(unitPrice.multiply(quantity));
        replay(line);
        return line;
    }
}
