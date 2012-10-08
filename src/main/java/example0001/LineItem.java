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

import java.math.BigDecimal;

/**
 * This interface defines a line item on an {@link Order}.  Line item
 * brings together a {@link Product} with a price and a quantity.
 */
public interface LineItem {
    Product getProduct();

    String getSKU();

    BigDecimal getQuantity();

    BigDecimal getUnitPrice();

    BigDecimal getExtendedPrice();
}
