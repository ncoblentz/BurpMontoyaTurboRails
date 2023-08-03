package com.nickcoblentz.montoya.turborails;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.proxy.websocket.ProxyWebSocketCreation;
import burp.api.montoya.proxy.websocket.ProxyWebSocketCreationHandler;
import com.nickcoblentz.montoya.utilities.LogHelper;

public class TurboRailsProxyWebSocketCreationHandler implements ProxyWebSocketCreationHandler {

    private final MontoyaApi _api;
    private static String _CableHttpHeaderName="Sec-WebSocket-Protocol";
    private static String _CableHttpHeaderValue="actioncable-v1-json, actioncable-unsupported";


    public TurboRailsProxyWebSocketCreationHandler(MontoyaApi api)
    {
        _api = api;

    }

    @Override
    public void handleWebSocketCreation(ProxyWebSocketCreation webSocketCreation) {
        LogHelper.GetInstance(_api).Debug("Handle Web Socket Created");
        for(HttpHeader httpHeader : webSocketCreation.upgradeRequest().headers())
        {
            LogHelper.GetInstance(_api).Debug(httpHeader.name()+": "+httpHeader.value());
            if(httpHeader.name().equals(_CableHttpHeaderName) && httpHeader.value().equals(_CableHttpHeaderValue))
            {
                LogHelper.GetInstance(_api).Debug("Yes");
                webSocketCreation.proxyWebSocket().registerProxyMessageHandler(new TurboRailsWebSocketProxyMessageHandler(_api));
            }
        }

    }
}
