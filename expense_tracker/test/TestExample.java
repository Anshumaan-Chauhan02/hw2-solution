// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }


    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }


    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }
    
    @Test
    public void testAddTransactionController() {
        // - Steps: Add a transaction with amount 50.00 and category ”food”
        // - Expected Output: Transaction is added to the table, Total Cost is updated

        double amount = 50.0;
        String category = "food";

        Integer total_row_count = view.getTableModel().getRowCount();
        double total_cost = view.getAmountField();

        controller.addTransaction(amount, category);

        assertEquals(total_row_count + 1, view.getTableModel().getRowCount());
        assertEquals(total_cost + amount, view.getAmountField(), 0.01);
    }

    @Test
    public void testInvalidInput() {
        // - Steps: Attempt to add a transaction with an invalid amount or category
        // - Expected Output: Error messages are displayed, transactions and Total Cost
        // remain unchanged
        double amount = -50.0; // negative value
        String category = "foodddd"; // invalid category name

        int total_row_count = view.getTableModel().getRowCount();
        double total_cost = view.getAmountField();

        controller.addTransaction(amount, category);

        // row count and total cost values should be unchangable
        assertEquals(total_row_count, view.getTableModel().getRowCount());
        assertEquals(total_cost, view.getAmountField());


        // TODO: check error joptionpane
        // Component[] components = view.getComponents(); // view inherited from JFrame

        // for (int i = 0; i < components.length; ++i) {
        // if ((components[i] instanceof JOptionPane)) {
        // boolean flag = true;
        // } else {
        // boolean flag = false;
        // }
        // }
    }

    @Test
    public void testUndoNotAllowed() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        controller.deleteTransaction(0);
        assertEquals(0, view.getTableModel().getRowCount());
        // TODO: check if a JOptionPane appears after undo in a empty list
    }

    @Test
    public void testUndoAllowed() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Perform the action: Add and remove a transaction
        double amount = 50.0;
        String category = "food";

        Transaction addedTransaction = new Transaction(amount, category);
        for (int i = 0; i < 2; i++) {
            controller.addTransaction(amount, category);
        }

        // Pre-condition: List of transactions contains only
        // the added transaction
        int transaction_size = model.getTransactions().size()
        assertEquals(3, transaction_size);
        Transaction firstTransaction = model.getTransactions().get(1);
        checkTransaction(amount, category, firstTransaction);

        double current_cost = getTotalCost();
        assertEquals(amount, getTotalCost(), 0.01);        

        // Perform the action: Remove the transaction
        // model.removeTransaction(addedTransaction);
        // should use something from the controller

        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(transaction_size - 1, transactions.size());

        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(current_cost - amount, totalCost, 0.01);

        // Check the view instead of the model stuffs
        assertEquals(current_cost - amount, view.getTableModel().getRowCount();, 0.01);
        assertEquals(transaction_size - 1, view.getAmountField());
    }

}
