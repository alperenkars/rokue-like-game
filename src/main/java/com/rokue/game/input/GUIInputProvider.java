package com.rokue.game.input;

import com.rokue.game.actions.IAction;
import java.util.Collections;
import java.util.List;

public class GUIInputProvider implements IInputProvider {
    @Override
    public List<IAction> pollActions() {
        return Collections.emptyList();
    }
}