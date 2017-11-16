package jinhaoxia.support.pattern.fpclose;

/* This file is copyright (c) 2008-2012 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/

import java.text.DecimalFormat;

/**
 * @author Philippe Fournier-Viger
 */
public abstract class AbstractItemset {

    public AbstractItemset() {
        super();
    }


    public abstract int size();

    public abstract String toString();


    public void print() {
        System.out.print(toString());
    }


    public abstract int getAbsoluteSupport();


    public abstract double getRelativeSupport(int nbObject);


    public String getRelativeSupportAsString(int nbObject) {
        // get the relative support
        double frequence = getRelativeSupport(nbObject);
        // convert it to a string with two decimals
        DecimalFormat format = new DecimalFormat();
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(5);
        return format.format(frequence);
    }


    public abstract boolean contains(Integer item);

}