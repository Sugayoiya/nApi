package kono.ene.napi.service.telegram.command.base;

public interface IOrderedCommand {

    int getOrder();

    String getGroup();
}
