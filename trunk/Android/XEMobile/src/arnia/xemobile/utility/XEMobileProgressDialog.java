package arnia.xemobile.utility;

import android.app.ProgressDialog;
import android.content.Context;

public class XEMobileProgressDialog {

	private static int progressDialogCount;
	private static ProgressDialog progress;

	public static void startProgress(Context context, String message) {
		progressDialogCount++;
		if (progressDialogCount == 1)
			progress = ProgressDialog.show(context, null, message, true, false);
	}

	public static void dismissProgress() {
		progressDialogCount--;
		if (progressDialogCount <= 0) {
			progress.dismiss();
			progressDialogCount = 0;
		}
	}

}
