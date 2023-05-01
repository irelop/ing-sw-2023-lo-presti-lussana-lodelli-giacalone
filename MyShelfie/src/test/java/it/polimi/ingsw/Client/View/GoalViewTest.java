package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.GoalAndScoreMsg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoalViewTest {
    GoalView view;
    GoalAndScoreMsg msg;

    @BeforeEach
    void setUp() {
        msg = new GoalAndScoreMsg(true, true, 12, true);
        view = new GoalView(msg);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void visual_test_function() { view.run(); }
}