/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.idgen.validator;

import org.openmrs.patient.UnallowedIdentifierException;
import org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator;

/**
 * The Verhoeff Check Digit Validator catches all single errors and all adjacent transpositions.
 * See: http://www.cs.utsa.edu/~wagner/laws/verhoeff.html and Wagner, Neal.
 * "Verhoeff's Decimal Error Detection". The Laws of Cryptography with Java Code. p 54. San Antonio,
 * TX: 2003. http://www.cs.utsa.edu/~wagner/lawsbookcolor/laws.pdf
 */
public class VerhoeffIdentifierValidatorRwanda extends BaseHyphenatedIdentifierValidator {
    
    private static final String ALLOWED_CHARS = "0123456789-";
    
    private static final String VERHOEFF_NAME = "Rwanda Verhoeff Check Digit Validator.";
    
    private static final int VERHOEFF_ID_LENGTH = 10;
    
    private static final int VERHOEFF_UNDECORATED_ID_LENGTH = VERHOEFF_ID_LENGTH - 2;
    
    /**
     * @see org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator#getAllowedCharacters()
     */
    @Override
    public String getAllowedCharacters() {
        return ALLOWED_CHARS;
    }
    
    /**
     * @see org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator#getName()
     */
    @Override
    public String getName() {
        return VERHOEFF_NAME;
    }
    
    /**
     * @see org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator#getCheckDigit(java.lang.String)
     */
    @Override
    protected int getCheckDigit(String undecoratedIdentifier) {
        int[] a = getBase(Integer.parseInt(undecoratedIdentifier), undecoratedIdentifier.length());
        insertCheck(a);
        return a[0];
    }
    
    
    @Override
    public String getValidIdentifier(String undecoratedIdentifier) throws UnallowedIdentifierException {
        //undecoratedIdentifier = fillInLeadingZeros(undecoratedIdentifier);
        checkAllowedIdentifier(undecoratedIdentifier);
        String checkNum = String.valueOf(getCheckDigit(undecoratedIdentifier));

        if (checkNum.equals("0"))
            checkNum = "A";
        if (checkNum.equals("1"))
            checkNum = "B";
        if (checkNum.equals("2"))
            checkNum = "C";
        if (checkNum.equals("3"))
            checkNum = "D";
        if (checkNum.equals("4"))
            checkNum = "E";
        if (checkNum.equals("5"))
            checkNum = "F";
        if (checkNum.equals("6"))
            checkNum = "G";
        if (checkNum.equals("7"))
            checkNum = "H";
        if (checkNum.equals("8"))
            checkNum = "I";
        if (checkNum.equals("9"))
            checkNum = "J";
        
        String result = undecoratedIdentifier + "-" + checkNum;
        return result;
    }
    
    @Override
    public boolean isValid(String identifier) throws UnallowedIdentifierException {
        
        if (identifier.indexOf("-") < 8) {
            throw new UnallowedIdentifierException("The base identifier must have at least 8 digits.");
        }
        
        String idWithoutCheckDigit = identifier.substring(identifier.indexOf("-") - 8, identifier.indexOf("-"));
        
        checkAllowedIdentifier(idWithoutCheckDigit);
        
        int computedCheckDigit = getCheckDigit(idWithoutCheckDigit);
        
        String checkDigit = identifier.substring(identifier.indexOf("-") + 1, identifier.length());
        
        if (checkDigit.length() != 1)
            throw new UnallowedIdentifierException("Identifier must have a check digit of length 1.");
        
        if (checkDigit.equalsIgnoreCase("A"))
            checkDigit = "0";
        if (checkDigit.equalsIgnoreCase("B"))
            checkDigit = "1";
        if (checkDigit.equalsIgnoreCase("C"))
            checkDigit = "2";
        if (checkDigit.equalsIgnoreCase("D"))
            checkDigit = "3";
        if (checkDigit.equalsIgnoreCase("E"))
            checkDigit = "4";
        if (checkDigit.equalsIgnoreCase("F"))
            checkDigit = "5";
        if (checkDigit.equalsIgnoreCase("G"))
            checkDigit = "6";
        if (checkDigit.equalsIgnoreCase("H"))
            checkDigit = "7";
        if (checkDigit.equalsIgnoreCase("I"))
            checkDigit = "8";
        if (checkDigit.equalsIgnoreCase("J"))
            checkDigit = "9";
        
        int givenCheckDigit = 10;
        
        try {
            givenCheckDigit = Integer.valueOf(checkDigit);
        }
        catch (NumberFormatException e) {
            throw new UnallowedIdentifierException(
                    "Check digit must either be a character from A to J or a single digit integer.");
        }
        
        return (computedCheckDigit == givenCheckDigit);
    }
    
    private int[] getBase(int num, int length) {
        int[] a = new int[length + 1];
        int x = 1;
        for (int i = length; i-- > 0;) {
            int y = num / x;
            a[i + 1] = y % 10;
            x = x * 10;
        }
        return a;
    }
    
    private int insertCheck(int[] a) {
        int check = 0;
        for (int i = 1; i < a.length; i++)
            check = op[check][F[i % 8][a[i]]];
        a[0] = inv[check];
        return a[0];
    }
    
    public VerhoeffIdentifierValidatorRwanda() {
        F[0] = F0;
        F[1] = F1;
        for (int i = 2; i < 8; i++) {
            F[i] = new int[10];
            for (int j = 0; j < 10; j++)
                F[i][j] = F[i - 1][F[1][j]];
        }
    }
    
    private int[][] F = new int[8][];
    
    private static final int[] F0 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    
    private static final int[] F1 = { 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 };
    
    private static final int[][] op = { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
            { 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 }, { 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 }, { 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 },
            { 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 }, { 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 }, { 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
            { 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 }, { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
    
    private static final int[] inv = { 0, 4, 3, 2, 1, 5, 6, 7, 8, 9 };
    
    private String fillInLeadingZeros(String undecoratedIdentifier){
        char[] ret = new char[VERHOEFF_UNDECORATED_ID_LENGTH];
        for (int i = 0; i < VERHOEFF_UNDECORATED_ID_LENGTH; i++){
            ret[i] = '0';
        }
        int i = VERHOEFF_UNDECORATED_ID_LENGTH - 1;
        for (int j = undecoratedIdentifier.length() - 1; j >= 0; j--){
            ret[i] = undecoratedIdentifier.charAt(j);
            i --;
            if (i < 0)
                break;
        }
        return String.valueOf(ret);
    }
}
