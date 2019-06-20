package Commands;

import Model.Model;

public class AutoRouteCommand implements Command {
    @Override
    public void doCommand(String[] array) {
        Model.turn=false;
    }
}
