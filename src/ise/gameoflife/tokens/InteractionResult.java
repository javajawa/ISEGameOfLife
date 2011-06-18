/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ise.gameoflife.tokens;

/**
 *
 * @author george
 */
public enum InteractionResult {
    LoanGiven, //Loan has been given to another group
    LoanTaken, //Group has received financial aid from another group
    NothingHappened, //Interaction didn't cause any changes
}
