package miga.timaxtex;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.TiMessenger;
import java.util.HashMap;
import android.os.Handler;
import android.os.Message;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.titanium.TiApplication;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;

@Kroll.module(name="Timaxtex", id="miga.timaxtex")
public class TiMaxTexModule extends KrollModule {

	public TiMaxTexModule() {
		super();
	}

	@Kroll.method
	public int getMaxTextureSize() {
		// approach adopted from: http://stackoverflow.com/questions/26985858/gles10-glgetintegerv-returns-0-in-lollipop-only
		EGLDisplay dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
		int[] vers = new int[2];
		EGL14.eglInitialize(dpy, vers, 0, vers, 1);

		int[] configAttr = {
			EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
			EGL14.EGL_LEVEL, 0,
			EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
			EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
			EGL14.EGL_NONE
		};
		EGLConfig[] configs = new EGLConfig[1];
		int[] numConfig = new int[1];
		EGL14.eglChooseConfig(dpy, configAttr, 0, configs, 0, 1, numConfig, 0);
		if (numConfig[0] == 0) {
		// TROUBLE! No config found.
		}
		EGLConfig config = configs[0];

		int[] surfAttr = {
			EGL14.EGL_WIDTH, 64,
			EGL14.EGL_HEIGHT, 64,
			EGL14.EGL_NONE
		};
		EGLSurface surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0);

		int[] ctxAttrib = {
			EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
			EGL14.EGL_NONE
		};
		EGLContext ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);
		EGL14.eglMakeCurrent(dpy, surf, surf, ctx);

		int[] maxSize = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);
		EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,EGL14.EGL_NO_CONTEXT);
		EGL14.eglDestroySurface(dpy, surf);
		EGL14.eglDestroyContext(dpy, ctx);
		EGL14.eglTerminate(dpy);
		return maxSize[0];
	}
}
