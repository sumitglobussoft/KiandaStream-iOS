<?php
// this file contains the contents of the popup window
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Insert Alert Box</title>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.js"></script>
<script language="javascript" type="text/javascript" src="tiny_mce_popup.js"></script>
<link rel="stylesheet" href="css/friendly_buttons_tinymce.css" />


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
		var text = jQuery('#button-dialog input#button-text').val();
		var type = jQuery('#button-dialog select#button-type').val();		 
		 
		var output = '';
		
		// setup the output of our shortcode
		output = '[alert ';
			output += 'type=' + type + ' ';

		// check to see if the TEXT field is blank
		if(text) {	
			output += ']'+ text + '[/alert]';
		}
		// if it is blank, use the selected text, if present
		else {
			output += ']'+ButtonDialog.local_ed.selection.getContent() + '[/alert]';
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
	<div id="button-dialog">
		<form action="/" method="get" accept-charset="utf-8" onsubmit="javascript:ButtonDialog.insert(ButtonDialog.local_ed);return false;">
			<div>
				<label for="button-text">Alert Text</label>
				<input type="text" name="button-text" value="" id="button-text" />
			</div>

			<div>
				<label for="button-type">Background color</label>
				<select name="button-type" id="button-type" size="1">
					<option value="white" selected="selected">White</option>
					<option value="red"=>Red</option>
					<option value="green"=>Green</option>
					<option value="yellow">Yellow</option>
					<option value="blue">Blue</option>
				</select>
			</div>
			<div>	
				<a href="javascript:ButtonDialog.insert(ButtonDialog.local_ed)" id="insert" style="display: block; line-height: 24px;">Insert</a>
			</div>
		</form>
	</div>
</body>
</html>