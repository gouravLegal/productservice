package com.example.productservice.testdemo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    public void whenAddTwoIntegersThenCorrectResultExpected() {
        //Arrange
        int a = 40;
        int b = 20;
        Calculator calc = new Calculator();

        //Act
        int result = calc.add(a, b);

        //Assert
        assertEquals(30, result);

    }

    @Test
    //Negative test case
    public void whenDivideByZeroThenThrowException() {
        //Arrange
        int a = 40;
        int b = 0;
        Calculator calc = new Calculator();

        //Act and Assert
        //Here we expect the function to throw an Arithmetic exception and so we are using assertThrows along with
        //first parameter being the Exception class thrown and second parameter being the method being invoked for testing
        assertThrows(ArithmeticException.class, () -> calc.divide(a, b));
    }

}