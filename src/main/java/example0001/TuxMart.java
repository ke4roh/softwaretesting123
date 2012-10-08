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

/**
 * This is a trivial store implementation to demonstrate techniques
 * for testing with mock objects and making those accessible to the
 * class under test.
 */
public class TuxMart {

    private InventoryService inventoryService;
    private PaymentService paymentService;
    private FinancialService financialService;

    /**
     * Conduct the business of a sale:
     * 1. Secure payment (this could be a credit card, receiving cash, or establishing that the user has credit)
     * 2. Record the event in the financial system
     * 3. Subtract stock from the inventory system
     *
     * @param o An order to process
     */
    public void makeSale(Order o) {
        if (getPaymentService().securePayment(o.getGrandTotal())) {
            getFinancialService().createOrder(o);
            for (LineItem li : o.getLineItems()) {
                getInventoryService().deductStock(li.getProduct(), li.getQuantity());
            }
        }
    }

    protected PaymentService getPaymentService() {
        return paymentService;
    }

    protected InventoryService getInventoryService() {
        return inventoryService;
    }

    protected FinancialService getFinancialService() {
        return financialService;
    }
}
