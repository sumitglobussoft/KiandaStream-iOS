<?php
// this file contains the contents of the popup window
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Highlight Text</title>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.js"></script>
<script language="javascript" type="text/javascript" src="tiny_mce_popup.js"></script>
<link rel="stylesheet" href="css/friendly_buttons_tinymce.css?version=3" />


<script type="text/javascript">
 
var ButtonDialog = {
	local_ed : 'ed',
	init : function(ed) {
		ButtonDialog.local_ed = ed;
		tinyMCEPopup.resizeToInnerSize();
	},
	insert : function insertButton(ed) {
	 
		// Try and remove existing style / blockquote
		tinyMCEPopup.execCommand('mceRemoveNode', false, null);
		 
		// set up variables to contain our input values
		var color = jQuery('#button-dialog-wide input#button-color').val();		 
		var text = jQuery('#button-dialog-wide input#button-text').val();		 
	 
		 
		var output = '';
		
		// setup the output of our shortcode
		output = '[highlight ';
		if(color) output += 'color=' + color + ' ';
		
		if(text) {	
			output += ']'+ text + '[/highlight]';
		}
		// if it is blank, use the selected text, if present
		else {
			output += ']'+ButtonDialog.local_ed.selection.getContent() + '[/highlight]';
		}
		tinyMCEPopup.execCommand('mceReplaceContent', false, output);
		 
		// Return
		tinyMCEPopup.close();
	}
};
tinyMCEPopup.onInit.add(ButtonDialog.init, ButtonDialog);
 
</script>

</head>
<body>
	<div id="button-dialog-wide">
		<form action="/" method="get" accept-charset="utf-8" onsubmit="javascript:ButtonDialog.insert(ButtonDialog.local_ed);return false;">
			<div>
				<label for="button-color">Background Color hex value:<br clear="both"/> <em>Leave blank for global color</em></label>
                <input type="text" name="button-color" value="" id="button-color" />
			</div>
            <br clear="both"/>
            <br clear="both"/>
			<div>
				<label for="button-text">Highlighted Text</label>
				<input type="text" name="button-text" value="" id="button-text" />
			</div>
			<div>	
				<a href="javascript:ButtonDialog.insert(ButtonDialog.local_ed)" id="insert" style="display: block; line-height: 24px;">Insert</a>
			</div>
		</form>
	</div>
</body>
</html>