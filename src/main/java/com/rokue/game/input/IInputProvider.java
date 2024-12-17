package com.rokue.game.input;

import com.rokue.game.actions.IAction;
import java.util.List;

public interface IInputProvider {
    List<IAction> pollActions();
}