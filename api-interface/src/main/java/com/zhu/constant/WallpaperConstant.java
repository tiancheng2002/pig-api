package com.zhu.constant;

import cn.hutool.core.util.RandomUtil;

public class WallpaperConstant {

    static final String[] wallpaperList = {
            "https://lz.sinaimg.cn/large/0072Vf1pgy1foxkil4o6qj31hc0u0nbz.jpg",
            "https://lz.sinaimg.cn/large/0072Vf1pgy1foxk6pmjkjj31kw0w0b0v.jpg",
            "https://lz.sinaimg.cn/large/0072Vf1pgy1foxki7zn13j31kw0w07uc.jpg",
            "https://lz.sinaimg.cn/large/006ZFECEgy1fr1x6gfxcrj31hc0u0e43.jpg",
            "https://lz.sinaimg.cn/large/87c01ec7gy1frkhy7h3aej21j00u0ng2.jpg"
    };

    public static String getRandomWallpaper(){
        int index = RandomUtil.randomInt(0, 5);
        return wallpaperList[index];
    }

}
