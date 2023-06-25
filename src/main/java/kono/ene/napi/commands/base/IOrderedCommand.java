package kono.ene.napi.commands.base;

public interface IOrderedCommand {

    int getOrder();

    String getGroup();
}
