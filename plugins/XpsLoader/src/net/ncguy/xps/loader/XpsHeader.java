package net.ncguy.xps.loader;

import java.util.Map;

public class XpsHeader {

    public int magicNumber;
    public int versionMajor;
    public int versionMinor;
    public String xnaAral;
    public int settingsLen;
    public String machine;
    public String user;
    public String files;
    public Object settings;
    public Map<String, BoneParser.BonePose> pose;
}
