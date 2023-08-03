package com.nickcoblentz.montoya.turborails;


import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.proxy.websocket.*;
import burp.api.montoya.websocket.*;
import com.nickcoblentz.montoya.utilities.LogHelper;
import com.nickcoblentz.montoya.utilities.RequestHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class TurboRailsWebSocketProxyMessageHandler implements ProxyMessageHandler {

    private final MontoyaApi _api;

    public TurboRailsWebSocketProxyMessageHandler(MontoyaApi api)
    {
        _api = api;
    }

    @Override
    public TextMessageReceivedAction handleTextMessageReceived(InterceptedTextMessage interceptedTextMessage) {
        return TextMessageReceivedAction.continueWith(interceptedTextMessage);
    }

    @Override
    public TextMessageToBeSentAction handleTextMessageToBeSent(InterceptedTextMessage interceptedTextMessage) {
        LogHelper.GetInstance(_api).Debug("Made it to handle text message to be sent");
        LogHelper.GetInstance(_api).Debug(interceptedTextMessage.payload());
        if(interceptedTextMessage.direction() == Direction.CLIENT_TO_SERVER && interceptedTextMessage.payload().contains("signed_stream_name"))
        {
            LogHelper.GetInstance(_api).Debug("Found signed stream name");
            String signedStreamNameEncoded=null;
            try {
                JSONObject bodyJson = new JSONObject(interceptedTextMessage.payload());
                String identifierString = bodyJson.getString("identifier");
                JSONObject identifierJson = new JSONObject(identifierString);
                signedStreamNameEncoded = identifierJson.getString("signed_stream_name");
                LogHelper.GetInstance(_api).Debug(signedStreamNameEncoded);

            }
            catch (JSONException e)
            {
                LogHelper.GetInstance(_api).Error(e.getMessage());
            }

            if(signedStreamNameEncoded!=null) {
                LogHelper.GetInstance(_api).Debug("Not Null");
                String[] signedStreamNameArray = signedStreamNameEncoded.split("--");
                if (signedStreamNameArray.length == 2) {
                    LogHelper.GetInstance(_api).Debug("Length is 2");
                    ByteArray signedStreamNameBytes = _api.utilities().base64Utils().decode(signedStreamNameArray[0]);
                    String signedStreamName = _api.utilities().byteUtils().convertToString(signedStreamNameBytes.getBytes());
                    LogHelper.GetInstance(_api).Debug("Prepending");
                    RequestHelper.PrependNote(interceptedTextMessage,"Signed Stream Name: "+signedStreamName);
                }
            }

        }
        return TextMessageToBeSentAction.continueWith(interceptedTextMessage);
    }

    @Override
    public BinaryMessageReceivedAction handleBinaryMessageReceived(InterceptedBinaryMessage interceptedBinaryMessage) {
        return BinaryMessageReceivedAction.continueWith(interceptedBinaryMessage);
    }

    @Override
    public BinaryMessageToBeSentAction handleBinaryMessageToBeSent(InterceptedBinaryMessage interceptedBinaryMessage) {
        return BinaryMessageToBeSentAction.continueWith(interceptedBinaryMessage);
    }
}
