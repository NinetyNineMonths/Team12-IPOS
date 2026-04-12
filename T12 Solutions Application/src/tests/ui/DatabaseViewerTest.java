package tests.ui;

import main.db.DatabaseManager;
import main.ui.DatabaseViewer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.GraphicsEnvironment;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class DatabaseViewerTest {

    @BeforeEach
    void setUp() {
        DatabaseManager.initialise();
    }

    // Expected: DatabaseViewer can be constructed and initial table load does not throw.
    @Test
    void testDatabaseViewer_Constructs() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping UI tests in headless environment.");
        DatabaseViewer frame = assertDoesNotThrow(DatabaseViewer::new);
        frame.dispose();
    }

    // Expected: database viewer includes selector and load control.
    @Test
    void testDatabaseViewer_HasSelectorAndLoadButton() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping UI tests in headless environment.");
        DatabaseViewer frame = new DatabaseViewer();
        try {
            List<JComboBox> selectors = UiTestUtils.findAllOfType(frame, JComboBox.class);
            assertEquals(1, selectors.size());
            assertEquals(8, selectors.get(0).getItemCount());
            assertTrue(UiTestUtils.findButtonByText(frame, "Load").isPresent());
        } finally {
            frame.dispose();
        }
    }

    // Expected: initial users table load creates visible table columns and rows.
    @Test
    void testDatabaseViewer_InitialLoadPopulatesTableModel() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Skipping UI tests in headless environment.");
        DatabaseViewer frame = new DatabaseViewer();
        try {
            List<JTable> tables = UiTestUtils.findAllOfType(frame, JTable.class);
            assertEquals(1, tables.size());

            TableModel model = tables.get(0).getModel();
            assertTrue(model.getColumnCount() > 0);
            assertTrue(model.getRowCount() >= 1, "Expected at least one seeded user row.");
        } finally {
            frame.dispose();
        }
    }
}
