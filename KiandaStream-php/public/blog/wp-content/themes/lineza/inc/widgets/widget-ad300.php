<?php

/*-----------------------------------------------------------------------------------

	Plugin Name: 300 Ad Widget
	Plugin URI: http://www.bloompixel.com
	Description: A widget that displays 300x250px ad.
	Version: 1.0
	Author: Simrandeep Singh
	Author URI: http://www.simrandeep.com

-----------------------------------------------------------------------------------*/

add_action( 'widgets_init', 'bp_300_ad_widget' );  

// Register Widget
function bp_300_ad_widget() {
    register_widget( 'bp_300_widget' );
}

// Widget Class
class bp_300_widget extends WP_Widget {

	// Widget Setup 
	function bp_300_widget() {
		$widget_ops = array( 'classname' => 'bp_300_widget', 'description' => __('A widget that displays 300x250 ad', 'bloompixel') );
		$control_ops = array( 'width' => '300', 'height' => '250', 'id_base' => 'bp_300_widget' );
		$this->WP_Widget( 'bp_300_widget', __('Lineza 300x250 Ad Widget', 'bloompixel'), $widget_ops, $control_ops );
	}
	
	function widget( $args, $instance ) {
		extract( $args );
		
		//Our variables from the widget settings.
		/* $title = apply_filters('widget_title', $instance['title'] ); */
		$banner = $instance['banner'];
		$link = $instance['link'];
		$ad_code = $instance['ad_code'];
		
		// Before Widget
		//echo $before_widget;
		
		// Display the widget title  
		/* if ( $title )
			echo $before_title . $title . $after_title; */
		
		?>
		<!-- START WIDGET -->
		<div class="ad-300-widget">
			<?php
				if ( $ad_code )
					echo '<div class="ad-block ad-block-300">' . $ad_code . '</div>';
					
				elseif ( $link )
					echo '<a href="' . $link . '"><img src="' . $banner . '" width="300" height="250" alt="" /></a>';
					
				elseif ( $banner )
					echo '<img src="' . $banner . '" width="300" height="250" alt="" />';
			?>
		</div>
		<!-- END WIDGET -->
		<?php
		
		// After Widget
		//echo $after_widget;
	}
	
	// Update the widget
	function update( $new_instance, $old_instance ) {
		$instance = $old_instance;
		
		/* $instance['title'] = strip_tags( $new_instance['title'] ); */
		$instance['link'] = $new_instance['link'];
		$instance['banner'] = $new_instance['banner'];
		$instance['ad_code'] = $new_instance['ad_code'];
		return $instance;
	}


	//Widget Settings
	function form( $instance ) {
		//Set up some default widget settings.
		$defaults = array(
			'link' => 'http://bloompixel.com/',
			'banner' => get_template_directory_uri()."/images/300x250.png",
			'ad_code' => '',
		);
		$instance = wp_parse_args( (array) $instance, $defaults );

		// Widget Title: Text Input
		?>
		<p>
			<label for="<?php echo $this->get_field_id( 'link' ); ?>"><?php _e('Ad Link URL:', 'bloompixel') ?></label>
			<input id="<?php echo $this->get_field_id( 'link' ); ?>" name="<?php echo $this->get_field_name( 'link' ); ?>" value="<?php echo esc_url($instance['link']); ?>" style="width:100%;" type="text" />
		</p>
		
		<p>
			<label for="<?php echo $this->get_field_id( 'banner' ); ?>"><?php _e('Ad Banner URL:', 'bloompixel') ?></label>
			<input id="<?php echo $this->get_field_id( 'banner' ); ?>" name="<?php echo $this->get_field_name( 'banner' ); ?>" value="<?php echo esc_url($instance['banner']); ?>" style="width:100%;" type="text" />
		</p>
		
		<p>
			<label for="<?php echo $this->get_field_id( 'ad_code' ); ?>"><?php _e('Ad Code (Google Adsense):', 'bloompixel') ?></label>
			<textarea id="<?php echo $this->get_field_id( 'ad_code' ); ?>" name="<?php echo $this->get_field_name( 'ad_code' ); ?>" cols="20" rows="10" class="widefat"><?php echo esc_html($instance['ad_code']); ?></textarea>
		</p>
		<?php
	}
}
?>