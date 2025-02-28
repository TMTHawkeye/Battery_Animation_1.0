package com.batterycharging.animation.chargingeffect;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.MutableLiveData;

import com.batterycharging.animation.chargingeffect.Activities.LauncherScreenActivity;
import com.batterycharging.animation.chargingeffect.Activities.SetAnimationAcivity;
import com.batterycharging.animation.chargingeffect.Activities.SetCreatedAnimationActivity;
import com.batterycharging.animation.chargingeffect.Activities.SetWallpaperActivity;

import org.jetbrains.annotations.NotNull;
import org.smrtobjads.ads.AdsParent;
import org.smrtobjads.ads.SmartAds;
import org.smrtobjads.ads.ads.AppOpenManager;
import org.smrtobjads.ads.ads.SmartAdsConfig;
import org.smrtobjads.ads.ads.SmartObjAdmob;
import org.smrtobjads.ads.ads.models.AdmobNative;
import org.smrtobjads.ads.adsutils.StorageCommon;

public class AdsClass extends AdsParent {
//    @NotNull
    public MutableLiveData<Boolean> isAdCloseSplash =new MutableLiveData<Boolean>();
     public MutableLiveData<AdmobNative> settingsNative = new MutableLiveData();

    @Override
    public void onCreate() {
        super.onCreate();
        adsClass = this;
        initAds();
    }

    private void initAds() {
        storageCommon = new StorageCommon();
        AppOpenManager.getInstance().setSplashAdId(BuildConfig.launcher_app_open);
        AppOpenManager.getInstance().init(this, BuildConfig.app_open_others);
        AppOpenManager.getInstance().disableAppResumeWithActivity(LauncherScreenActivity.class);
        AppOpenManager.getInstance().disableAppResumeWithActivity(SetAnimationAcivity.class);
        AppOpenManager.getInstance().disableAppResumeWithActivity(SetWallpaperActivity.class);
        AppOpenManager.getInstance().disableAppResumeWithActivity(SetCreatedAnimationActivity.class);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        String environment = BuildConfig.env_dev ? SmartAdsConfig.ENVIRONMENT_DEVELOP : SmartAdsConfig.ENVIRONMENT_PRODUCTION;
        smartAdsConfig = new SmartAdsConfig(this, SmartAdsConfig.PROVIDER_ADMOB, environment);
//        smartAdsConfig.setIdAdResume(BuildConfig.app_open_others);

        SmartAds.getInstance().init(this, smartAdsConfig, false);

        SmartObjAdmob.getInstance().setDisableAdResumeWhenClickAds(true);
        SmartObjAdmob.getInstance().setOpenActivityAfterShowInterAds(false);

        AppOpenManager.getInstance().setResumeAdsRequestLimit(6);
        SmartAds.getInstance().setCountClickToShowAds(3);
        // initBilling();
    }

    /*private void initBilling() {
        List<PurchaseItem> listPurchaseItem = new ArrayList<>();
        listPurchaseItem.add(new PurchaseItem("PRODUCT_ID", AppPurchase.TYPE_IAP.PURCHASE));
        listPurchaseItem.add(new PurchaseItem("ID_SUBS_WITH_FREE_TRIAL", "trial_id", AppPurchase.TYPE_IAP.SUBSCRIPTION));
        listPurchaseItem.add(new PurchaseItem("ID_SUBS_WITHOUT_FREE_TRIAL", AppPurchase.TYPE_IAP.SUBSCRIPTION));
        AppPurchase.getInstance().initBilling(this, listPurchaseItem);

    }*/

    static AdsClass adsClass;

    @NotNull
    public static AdsClass getAdApplication() {
        return adsClass;
    }

    StorageCommon storageCommon;

    @NotNull
    public StorageCommon getStorageCommon() {
        return storageCommon;
    }



}