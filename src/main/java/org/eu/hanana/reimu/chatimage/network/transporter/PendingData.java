package org.eu.hanana.reimu.chatimage.network.transporter;

public class PendingData {
    public String key;
    public boolean send;
    public long startTime;
    public FtpInputStream ftpInputStream;
}
