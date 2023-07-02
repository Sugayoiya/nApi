package kono.ene.napi.service.telegram.commands.base;

public interface IOrderedCommand {

    int getOrder();

    String getGroup();
}
