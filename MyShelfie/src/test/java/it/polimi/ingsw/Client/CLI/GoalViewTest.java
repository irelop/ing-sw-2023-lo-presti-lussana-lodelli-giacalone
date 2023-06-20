package it.polimi.ingsw.Client.CLI;

import it.polimi.ingsw.Client.view.CLI.GoalView;
import it.polimi.ingsw.Server.Messages.GoalAndScoreMsg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GoalViewTest {
    GoalView view;
    GoalAndScoreMsg msg;

    @BeforeEach
    void setUp() {
        msg = new GoalAndScoreMsg(true, true, 12, true, false);
        view = new GoalView(msg);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void visual_test_function() { view.run(); }
}