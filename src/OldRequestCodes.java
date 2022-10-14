public enum OldRequestCodes {
    /**
     * AGC pings all on your network with this request
     * so they respond and it can connect to them :D
     */
    FINDING_DEVICES,
    /**
     * When a client wants to connect, they send this request:
     */
    ADD_ME,
    /**
     * The server application is exiting.
     */
    SERVER_CLOSE,
    /**
     * The client application is exiting.
     */
    CLIENT_CLOSE;
    
}
