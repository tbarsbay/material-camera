package com.affolestad.easyvideoplayer;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 * @author Aidan Follestad (afollestad)
 */
interface IUserMethods {

    void setSource(@NonNull Uri source);

    void setCallback(@NonNull EasyVideoCallback callback);

    void setProgressCallback(@NonNull EasyVideoProgressCallback callback);

    void setLeftAction(@EasyVideoPlayer.LeftAction int action);

    void setRightAction(@EasyVideoPlayer.RightAction int action);

    void setRetryText(@Nullable CharSequence text);

    void setRetryTextRes(@StringRes int res);

    void setSubmitText(@Nullable CharSequence text);

    void setSubmitTextRes(@StringRes int res);

    void setPromptText(@Nullable CharSequence text);

    void setPromptTextRes(@StringRes int res);

    void setPlayDrawable(@NonNull Drawable drawable);

    void setPlayDrawableRes(@DrawableRes int res);

    void setPauseDrawable(@NonNull Drawable drawable);

    void setPauseDrawableRes(@DrawableRes int res);

    void setHideControlsOnPlay(boolean hide);

    void setAutoPlay(boolean autoPlay);

    void setInitialPosition(@IntRange(from = 0, to = Integer.MAX_VALUE) int pos);

    void setHideTimeProgress(boolean hide);

    void showControls();

    void hideControls();

    void hideTimeProgress();

    @CheckResult
    boolean isControlsShown();

    void toggleControls();

    void enableControls(boolean andShow);

    void disableControls();

    @CheckResult
    boolean isPrepared();

    @CheckResult
    boolean isPlaying();

    @CheckResult
    int getCurrentPosition();

    @CheckResult
    int getDuration();

    void start();

    void seekTo(@IntRange(from = 0, to = Integer.MAX_VALUE) int pos);

    void setVolume(@FloatRange(from = 0f, to = 1f) float leftVolume, @FloatRange(from = 0f, to = 1f) float rightVolume);

    void pause();

    void stop();

    void reset();

    void release();
}