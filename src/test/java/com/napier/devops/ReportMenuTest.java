package com.napier.devops;

import org.junit.jupiter.api.Test;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

public class ReportMenuTest {

    @Test
    void validChoiceReturnsSelection() {
        ReportMenu.ReportSelection selection =
                ReportMenu.showReportMenu(new Scanner("5\n"));
        assertNotNull(selection);
        assertEquals(5, selection.getReportNumber());
    }

    @Test
    void zeroExitsMenu() {
        assertNull(ReportMenu.showReportMenu(new Scanner("0\n")));
    }

    @Test
    void invalidInputIsRetriedUntilValid() {
        ReportMenu.ReportSelection selection =
                ReportMenu.showReportMenu(new Scanner("abc\n99\n32\n"));
        assertNotNull(selection);
        assertEquals(32, selection.getReportNumber());
    }

    @Test
    void noInputReturnsNull() {
        assertNull(ReportMenu.showReportMenu(new Scanner("")));
    }
}