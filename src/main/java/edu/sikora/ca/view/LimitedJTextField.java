package edu.sikora.ca.view;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Klasa jest szczególną wersją JTextField nie pozwalającą na wpisywanie znaków specjalnych.
 * Domyślnie klasa nie zezwala na wpisywanie liter, ale pozwala na wprowadzenie kropki.
 *
 * @author Kamil Sikora
 */
public class LimitedJTextField extends JTextField {
    static String BADCHARS = "`~!@#$%^&*()[]_+=\\|\"':;?/><, ";
    boolean allowLetters = false;

    public LimitedJTextField() {
    }

    /**
     * @param disallowChar znaki zapisane w Stringu, które nie będę przyjmowane.
     */
    public LimitedJTextField(String disallowChar) {
        BADCHARS += disallowChar;
    }

    /**
     * @param allowLetters po wprowadzeniu flagi true, pole będzie przyjmować znaki alfanumeryczne.
     */
    public LimitedJTextField(boolean allowLetters) {
        this.allowLetters = allowLetters;
    }

    /**
     * @param text    tekst początkowy
     * @param columns liczba kolumn
     */
    public LimitedJTextField(String text, int columns) {
        super(text, columns);
    }

    /**
     * @param text         tekst początkowy
     * @param columns      liczba kolumn
     * @param allowLetters po wprowadzeniu flagi true, pole będzie przyjmować znaki alfanumeryczne.
     */
    public LimitedJTextField(String text, int columns, boolean allowLetters) {
        super(text, columns);
        this.allowLetters = allowLetters;
    }

    public void processKeyEvent(KeyEvent e) {
        char c = e.getKeyChar();
        if (allowLetters) {
            if (BADCHARS.indexOf(c) > -1 && !Character.isSpaceChar(c)) {
                e.consume();
                return;
            }
            super.processKeyEvent(e);
            return;
        }

        if ((Character.isLetter(c) && !e.isAltDown())
                || BADCHARS.indexOf(c) > -1) {
            e.consume();
            return;
        }
        if (c == '-' && getDocument().getLength() > 0)
            e.consume();
        else super.processKeyEvent(e);
    }


}