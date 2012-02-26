/* 
* @(#)JavaByteArray2SqlByteArrayFieldConversion.java 
* 
* Copyright 2009 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package myorg.persistenceTier;

import myorg.domain.util.ByteArray;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * 
 * @author  Paulo Abrantes
 * @author  Luis Cruz
 * 
*/
public class JavaByteArray2SqlByteArrayFieldConversion implements FieldConversion {

    public Object javaToSql(Object source) {
        if (source instanceof ByteArray) {
            final ByteArray byteArray = (ByteArray) source;
            return byteArray.getBytes();
        }
        return source;
    }

    public Object sqlToJava(Object source) {
        if (source instanceof byte[]) {            
            return new ByteArray((byte[]) source);
        }
        return source;
    }

}
