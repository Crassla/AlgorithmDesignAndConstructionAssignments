/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment4;

import java.text.DecimalFormat;

/**
 *
 * @author alex
 */
public class CurrencyEdge {

    public String element1;
    public String element2;
    public double exchangePer;
    public double exchangeRate;
    
    public DecimalFormat df = new DecimalFormat("#0.000");

    public CurrencyEdge(String element1, String element2, double exchangePer, double exchangeRate) {
        this.element1 = element1;
        this.element2 = element2;
        this.exchangePer = exchangePer;
        this.exchangeRate = exchangeRate;
    }
    
    @Override
    public String toString() {
        return "Element: " + this.element1 + "-" + this.element2 + " Percentage: " + df.format(this.exchangePer);
    }
    
    public String printConnection() {
        return this.element1 + "-" + this.element2;
    }
}
