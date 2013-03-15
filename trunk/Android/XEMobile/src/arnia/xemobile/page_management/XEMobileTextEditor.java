package arnia.xemobile.page_management;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;
import arnia.xemobile.R;
import arnia.xemobile.XEFragment;

public class XEMobileTextEditor extends XEFragment implements OnClickListener,
		TextWatcher {

	// Editor control
	private EditText txtContent;
	private ToggleButton btnBold;
	private ToggleButton btnItalic;
	private ToggleButton btnUnderline;
	private Button btnLink;
	private Button btnAddImage;

	private int styleStart = -1;
	private int cursorLoc = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.xemobiletexteditor, container,
				false);

		// Editor control
		txtContent = (EditText) view.findViewById(R.id.XEMOBILE_EDITOR_CONTENT);
		txtContent.addTextChangedListener(this);

		btnBold = (ToggleButton) view.findViewById(R.id.XEMOBILE_EDITOR_BOLD);
		btnBold.setOnClickListener(this);
		btnItalic = (ToggleButton) view
				.findViewById(R.id.XEMOBILE_EDITOR_ITALIC);
		btnItalic.setOnClickListener(this);
		btnUnderline = (ToggleButton) view
				.findViewById(R.id.XEMOBILE_EDITOR_UNDERLINE);
		btnUnderline.setOnClickListener(this);
		btnLink = (Button) view.findViewById(R.id.XEMOBILE_EDITOR_LINK);
		btnLink.setOnClickListener(this);
		btnAddImage = (Button) view
				.findViewById(R.id.XEMOBILE_EDITOR_ADD_IMAGE);
		btnAddImage.setOnClickListener(this);

		return view;
	}

	@Override
	public void afterTextChanged(Editable s) {
		int position = Selection.getSelectionStart(txtContent.getText());

		if (position < 0) {
			position = 0;
		}

		if (position > 0) {

			if (styleStart > position || position > (cursorLoc + 1)) {
				// user changed cursor location, reset
				if (position - cursorLoc > 1) {
					// user pasted text
					styleStart = cursorLoc;
				} else {
					styleStart = position - 1;
				}
			}

			if (btnBold.isChecked()) {
				StyleSpan[] ss = s.getSpans(styleStart, position,
						StyleSpan.class);

				for (int i = 0; i < ss.length; i++) {
					if (ss[i].getStyle() == android.graphics.Typeface.BOLD) {
						s.removeSpan(ss[i]);
					}
				}
				s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
						styleStart, position,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (btnItalic.isChecked()) {
				StyleSpan[] ss = s.getSpans(styleStart, position,
						StyleSpan.class);

				for (int i = 0; i < ss.length; i++) {
					if (ss[i].getStyle() == android.graphics.Typeface.ITALIC) {
						s.removeSpan(ss[i]);
					}
				}
				s.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),
						styleStart, position,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (btnUnderline.isChecked()) {
				UnderlineSpan[] ss = s.getSpans(styleStart, position,
						UnderlineSpan.class);

				for (int i = 0; i < ss.length; i++) {
					s.removeSpan(ss[i]);
				}
				s.setSpan(new UnderlineSpan(), styleStart, position,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		cursorLoc = Selection.getSelectionStart(txtContent.getText());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.XEMOBILE_EDITOR_BOLD:
			boldClick();
			break;

		case R.id.XEMOBILE_EDITOR_ITALIC:
			italicClick();
			break;
		case R.id.XEMOBILE_EDITOR_UNDERLINE:
			underlineClick();
			break;
		}
	}

	private void boldClick() {
		int selectionStart = txtContent.getSelectionStart();

		styleStart = selectionStart;

		int selectionEnd = txtContent.getSelectionEnd();

		if (selectionStart > selectionEnd) {
			int temp = selectionEnd;
			selectionEnd = selectionStart;
			selectionStart = temp;
		}

		if (selectionEnd > selectionStart) {
			Spannable str = txtContent.getText();
			StyleSpan[] ss = str.getSpans(selectionStart, selectionEnd,
					StyleSpan.class);

			boolean exists = false;
			for (int i = 0; i < ss.length; i++) {
				if (ss[i].getStyle() == android.graphics.Typeface.BOLD) {
					str.removeSpan(ss[i]);
					exists = true;
				}
			}

			if (!exists) {
				str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
						selectionStart, selectionEnd,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			btnBold.setChecked(false);
		}
	}

	private void italicClick() {
		int selectionStart = txtContent.getSelectionStart();

		styleStart = selectionStart;

		int selectionEnd = txtContent.getSelectionEnd();

		if (selectionStart > selectionEnd) {
			int temp = selectionEnd;
			selectionEnd = selectionStart;
			selectionStart = temp;
		}

		if (selectionEnd > selectionStart) {
			Spannable str = txtContent.getText();
			StyleSpan[] ss = str.getSpans(selectionStart, selectionEnd,
					StyleSpan.class);

			boolean exists = false;
			for (int i = 0; i < ss.length; i++) {
				if (ss[i].getStyle() == android.graphics.Typeface.ITALIC) {
					str.removeSpan(ss[i]);
					exists = true;
				}
			}

			if (!exists) {
				str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),
						selectionStart, selectionEnd,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			btnItalic.setChecked(false);
		}
	}

	private void underlineClick() {
		int selectionStart = txtContent.getSelectionStart();

		styleStart = selectionStart;

		int selectionEnd = txtContent.getSelectionEnd();

		if (selectionStart > selectionEnd) {
			int temp = selectionEnd;
			selectionEnd = selectionStart;
			selectionStart = temp;
		}

		if (selectionEnd > selectionStart) {
			Spannable str = txtContent.getText();
			UnderlineSpan[] ss = str.getSpans(selectionStart, selectionEnd,
					UnderlineSpan.class);

			boolean exists = false;
			for (int i = 0; i < ss.length; i++) {
				str.removeSpan(ss[i]);
				exists = true;
			}

			if (!exists) {
				str.setSpan(new UnderlineSpan(), selectionStart, selectionEnd,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			btnUnderline.setChecked(false);
		}
	}

}
