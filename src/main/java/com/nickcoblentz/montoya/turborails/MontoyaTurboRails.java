package com.nickcoblentz.montoya.turborails;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import com.nickcoblentz.montoya.utilities.LogHelper;
public class MontoyaTurboRails implements BurpExtension {

    private MontoyaApi _api;
    private TurboRailsProxyWebSocketCreationHandler _WSCreationHandler;

    public void initialize(MontoyaApi api) {
        _api = api;
        LogHelper loghelper = LogHelper.GetInstance(api);
        loghelper.SetLevel(LogHelper.LogLevel.INFO);
        loghelper.Info("Plugin Loading...");
        api.extension().setName("TurboRails Extension");
        _WSCreationHandler = new TurboRailsProxyWebSocketCreationHandler(api);
        api.proxy().registerWebSocketCreationHandler(_WSCreationHandler);
        loghelper.Info("Plugin Loaded");
    }
}
