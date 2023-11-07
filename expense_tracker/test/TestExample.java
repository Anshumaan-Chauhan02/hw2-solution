// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import javax.swing.*;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;
    
  // Changed to BeforeAll so that we do not have to do cleanup 
  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
  }

// A precautionary cleanup step (even though setup resets model, view and controller before each test run)
  @After
  public void cleanup()
  {
    model = null;
    view = null;
    controller = null;
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
    public void testAddTransactionView_N() {
        // - Steps: Add a transaction with amount 50.00 and category ”food”
        // - Expected Output: Transaction is added to the table, Total Cost is updated in the Table Model

        // Precondition, the model should be empty
        assertEquals(0, model.getTransactions().size());

        double amount = 50.0;
        String category = "food";
        
        // Adding a new transaction to the view (through controller)
        controller.addTransaction(amount, category);

        // Postconditions, transaction shpuld be added in the table and the total cost should be updated
        // asserting that the changes are observed in the Table Model
        // One row for transaction and one row for the total cost row 
        assertEquals(2, view.getTableModel().getRowCount());

        // asserting changes are observed in the JTable
        assertEquals(amount, (double) view.getJTransactionsTable().getValueAt(0,1), 0.01);
        assertEquals(category, (String) view.getJTransactionsTable().getValueAt(0,2));
    
        // asserting that the total cost is correctly updated in the View
        assertEquals(amount, (double) view.getTableModel().getValueAt(1,3), 0.01);
    }

    @Test
    public void testAddTransactionView_E() {
        // - Steps: Try to add an invalid transaction with amount -50.00 and category ”food”
        // - Expected Output: View remains same - no changes, as the transaction is invalid

        // Precondition, the model should be empty
        assertEquals(0, model.getTransactions().size());

        double amount = -50.0;
        String category = "food";
        
        // Trying to add an invalid new transaction to the view (through controller)
        controller.addTransaction(amount, category);

        // Postconditions, transaction should make no changes to the table and Table Model
        // asserting that no changes are observed in the Table Model (it is still empty)
        assertEquals(0, view.getTableModel().getRowCount());

        // asserting JTable is empty
        assertEquals(0, view.getJTransactionsTable().getRowCount());
    }

    
    // @Test
    // public void testFilterAmount()
    // {
    //     double amount_1 = 50.0;
    //     String category_1 = "food";

    //     double amount_2 = 120.0;
    //     String category_2 = "travel";

    //     double amount_3 = 100.0;
    //     String category_3 = "food";

    //     controller.addTransaction(amount_1, category_1);
    //     controller.addTransaction(amount_2, category_2);
    //     controller.addTransaction(amount_3, category_3);

    //     double filter_amount = 100.0;

    //     AmountFilter amountFilter = new AmountFilter(filter_amount);
    //     controller.setFilter(amountFilter);
    //     controller.applyFilter();

    //     for(int col=0; i<=3;col++)
    //     {
    //         assertEquals(new Color(173, 255, 168), get_jtable_cell_component(view.getTransactionsTable(), 2, col));
    //     }
    
    // }

    // @Test
    // public void testFilterCategory()
    // {
    //     double amount_1 = 50.0;
    //     String category_1 = "food";

    //     double amount_2 = 120.0;
    //     String category_2 = "travel";

    //     double amount_3 = 100.0;
    //     String category_3 = "food";

    //     controller.addTransaction(amount_1, category_1);
    //     controller.addTransaction(amount_2, category_2);
    //     controller.addTransaction(amount_3, category_3);

    //     double filter_category = "travel";

    //     CategoryFilter categoryFilter = new CategoryFilter(filter_category);
    //     controller.setFilter(amountFilter);
    //     controller.applyFilter();

    //     for(int col=0; i<=3;col++)
    //     {
    //         assertEquals(new Color(173, 255, 168), get_jtable_cell_component(view.getTransactionsTable(), 1, col));
    //     }
    
    // }

    // public Color get_jtable_cell_component(JTable jtable, int row, int column){
    //     renderer = jtable.getCellRenderer(row, column);
    //     value = jtable.getModel().getValueAt(row, column);
    //     selectedColor = jtable.getSelectionModel().isSelectedIndex(row);
    //     hasFocus = True;
    //     component = renderer.getTableCellRendererComponent(jtable, value, selectedColor, hasFocus, row, column);
    //     return component.getBackground();
    // }


    // @Test
    // public void testUndoNotAllowed() {
    //     // Pre-condition: List of transactions is empty
    //     assertEquals(0, model.getTransactions().size());

    //     controller.deleteTransaction(0);
    //     assertEquals(0, view.getTableModel().getRowCount());
    //     // TODO: check if a JOptionPane appears after undo in a empty list
    // }

    // @Test
    // public void testUndoAllowed() {
    //     // Pre-condition: List of transactions is empty
    //     assertEquals(0, model.getTransactions().size());

    //     // Perform the action: Add and remove a transaction
    //     double amount = 50.0;
    //     String category = "food";

    //     Transaction addedTransaction = new Transaction(amount, category);
    //     for (int i = 0; i < 2; i++) {
    //         controller.addTransaction(amount, category);
    //     }
    //     // Pre-condition: List of transactions contains only
    //     // the added transaction
    //     int transaction_size = model.getTransactions().size()
    //     assertEquals(3, transaction_size);
    //     Transaction firstTransaction = model.getTransactions().get(1);
    //     checkTransaction(amount, category, firstTransaction);

    //     double current_cost = getTotalCost();
    //     assertEquals(amount, getTotalCost(), 0.01);        

    //     // Perform the action: Remove the transaction
    //     // model.removeTransaction(addedTransaction);
    //     // should use something from the controller

    //     // Post-condition: List of transactions is empty
    //     List<Transaction> transactions = model.getTransactions();
    //     assertEquals(transaction_size - 1, transactions.size());

    //     // Check the total cost after removing the transaction
    //     double totalCost = getTotalCost();
    //     assertEquals(current_cost - amount, totalCost, 0.01);

    //     // Check the view instead of the model stuffs
    //     assertEquals(current_cost - amount, view.getTableModel().getRowCount();, 0.01);
    //     assertEquals(transaction_size - 1, view.getAmountField());
    // }

}
