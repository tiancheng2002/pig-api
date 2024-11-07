package com.zhu.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zhu.constant.WallpaperConstant;
import com.zhu.modal.entity.Wallpaper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallpaper")
public class WallpaperController {

    @GetMapping("/")
    public Wallpaper getRandomWallpaper(){
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setImgUrl(WallpaperConstant.getRandomWallpaper());
        return wallpaper;
    }

}
