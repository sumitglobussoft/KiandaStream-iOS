<?php
/*
Plugin Name: Bl Post
Description: Display posts
Author: Ivar Rafn
Version: 1
Author URI: http://www.bluth.is/
*/
class bl_posts extends WP_Widget {

	function bl_posts(){
		$widget_ops = array('classname' => 'bl_posts', 'description' => 'Display posts' );
		$this->WP_Widget('bl_posts', 'Bluthemes - Posts', $widget_ops);
	}


	function widget( $args, $instance ) {

		global $cat_menu;

		#
		#	THE SETUP
		#

		extract($args);
		extract($instance);
		echo $before_widget;
		
		#
		#	THE TITLE
		#

		$title_output = '';
		if(!empty($title)){ 
			// $title_output .= '<div class="title-area">';
			$title_output .= 	'<h3 class="widget-head clearfix">';
			$title_output .= 		'<span>' . $title . '</span>';
			$title_output .= 		'<small>' . $subtitle . '</small>';
			if( $instance['loadmorebutton'] == 'true'){

				$title_output .= 		'<div class="orderposts pull-right ">';
				$title_output .= 			'<button class="btn toggle-orderposts " type="button">';
                $title_output .= 				'<i class="fa fa-circle"></i><i class="fa fa-circle"></i><i class="fa fa-circle"></i>';
                $title_output .= 			'</button>';
                $title_output .= 			'<div class="orderposts-body">';
				$title_output .= 				'<a href="#" data-orderby="title" data-order="ASC" class="orderposts-btn btn signature-animation">' . __('NAME', 'bluth') . '</a>';
				$title_output .= 				'<a href="#" data-orderby="date" class="orderposts-btn btn signature-animation">' . __('NEWEST', 'bluth') . '</a>';
				$title_output .= 			'</div>';
				$title_output .= 		'</div>';
			}
			$title_output .= 	'</h3>';
		}
		$title_output .= 	'<div class="box pad-xs-10 pad-sm-15 pad-md-20 clearfix">';
		echo html_entity_decode($title_output);

		// $title 	= apply_filters( 'widget_title', empty( $title ) ? '' : $title, $instance, $this->id_base );

		#
		#	THE QUERY
		#

		// categories
		$cat_posts 		= (empty($cat_posts) or (is_array($cat_posts) and in_array('0', $cat_posts))) ? '0' : $cat_posts;
		$tag_posts 		= (empty($tag_posts) or (is_array($tag_posts) and in_array('0', $tag_posts))) ? '0' : $tag_posts;
		$posts_per_page = empty($posts_per_page) ? 3 : $posts_per_page;
		$orderby 		= empty($orderby) ? 'date' : $orderby;
		$order 			= empty($order) ? 'desc' : $order;

	    $args = array(
	    	'offset' 				=> 0,
			'posts_per_page' 		=> $posts_per_page,
			'display_excerpt' 		=> $display_excerpt,
			'display_author' 		=> empty($display_author) ? 'true' : $display_author,
			'display_date' 			=> empty($display_date) ? 'true' : $display_date,
			'display_duplicates'	=> empty($display_duplicates) ? 'true' : $display_duplicates,
			'loadmorebutton' 		=> $loadmorebutton,
    		'ignore_sticky_posts'	=> 1, 
    		'category__in'			=> $cat_posts,
    		'orderby' 				=> $orderby, 
    		'order' 				=> $order, 
    	);
    	if(!empty($tag_posts)) $args['tag_slug__in'] = $tag_posts;

	    if(!empty($cat_posts) and is_array($cat_posts)){
			foreach($cat_posts as $cat_id) {
			 	$cat_name = get_cat_name($cat_id);
				$cat_menu[$cat_id] = $cat_name;
			} 
	    }

	    #
	    # POSTS PER ROW
	    #
	    	$custom_css = 'col-sm-6 col-md-6 col-lg-6';
	    	switch($posts_per_row){
	    		case '1':
	    			$custom_css = 'col-sm-12 col-md-12 col-lg-12';
	    		break;
	    		case '2':
	    			$custom_css = 'col-sm-6 col-md-6 col-lg-6';
	    		break;
	    		case '3':
	    			$custom_css = 'col-sm-4 col-md-4 col-lg-4';
	    		break;
	    		case '4':
	    			$custom_css = 'col-sm-3 col-md-3 col-lg-3';
	    		break;
	    	}

		echo blu_ajaxload_posts('grid', $args, $custom_css);

		echo '</div>';
		echo $after_widget;
	}

	function update( $new_instance, $old_instance ) {
		
		$instance = $old_instance;
		foreach($new_instance as $key => $value){
			$instance[$key] = $value;
		}
		return $instance;
	}

	function form( $instance ) {
		wp_enqueue_script( 'suggest' );
		$instance = wp_parse_args( (array) $instance, array( 'title' => '', 'subtitle' => '', 'posts_per_page' => 3, 'posts_per_row' => 2, 'display_excerpt' => true, 'display_author' => true, 'display_date' => true, 'display_duplicates' => true, 'loadmorebutton' => true, 'cat_posts' => '', 'tag_posts' => '', 'orderby' => 'date', 'order' => 'desc' ) );

		extract($instance); ?>
		
		<div class="full" style="margin-top: 20px; margin-bottom: 10px;">
			<div class="half">
				<label class="customlabel" for="<?php echo $this->get_field_id('Title'); ?>">
					<?php _e('Title:', 'bluth_admin'); ?>
					<small><?php _e('Title (optional)', 'bluth_admin'); ?></small>
				</label>
			</div>
			<div class="half">
				<input class="normal" id="<?php echo $this->get_field_id('title'); ?>" name="<?php echo $this->get_field_name('title'); ?>" type="text" value="<?php echo esc_attr($title); ?>" />
			</div>
		</div>
		<div class="full" style="margin-bottom: 10px;">
			<div class="half">
				<label class="customlabel" for="<?php echo $this->get_field_id('subtitle'); ?>">
					<?php _e('Sub-Title:', 'bluth_admin'); ?>
					<small><?php _e('Extra Title (optional)', 'bluth_admin'); ?></small>
				</label>
			</div>
			<div class="half">
				<input class="normal" id="<?php echo $this->get_field_id('subtitle'); ?>" name="<?php echo $this->get_field_name('subtitle'); ?>" type="text" value="<?php echo esc_attr($subtitle); ?>" />
			</div>
		</div>

		<!-- POSTS PER PAGE -->
		<div class="full" style="margin-bottom: 10px;">
			<div class="half">
				<label class="customlabel" for="<?php echo $this->get_field_id('posts_per_page'); ?>">
					<?php _e('Posts Per Load:', 'bluth_admin'); ?>
					<small><?php _e('How many posts to load', 'bluth_admin'); ?></small>
				</label>
			</div>
			<div class="half">
				<input class="normal" id="<?php echo $this->get_field_id('posts_per_page'); ?>" name="<?php echo $this->get_field_name('posts_per_page'); ?>" type="text" value="<?php echo esc_attr($posts_per_page); ?>" />
			</div>
		</div>

		<!-- POSTS PER ROW -->
		<div class="full" style="margin-bottom: 10px;">
			<div class="half">
				<label class="customlabel" for="<?php echo $this->get_field_id('posts_per_row'); ?>">
					<?php _e('Posts Per Row:', 'bluth_admin'); ?>
					<small><?php _e('How many posts in each row', 'bluth_admin'); ?></small>
				</label>
			</div>
			<div class="half">
				<select class="normal" id="<?php echo $this->get_field_id('posts_per_row'); ?>" name="<?php echo $this->get_field_name('posts_per_row'); ?>">
					<option value="1"<?php selected($instance['posts_per_row'], '1') ?>>1</option>
					<option value="2"<?php selected($instance['posts_per_row'], '2') ?>>2</option>
					<option value="3"<?php selected($instance['posts_per_row'], '3') ?>>3</option>
					<option value="4"<?php selected($instance['posts_per_row'], '4') ?>>4</option>
				</select>
			</div>
		</div>

		<!-- DISPLAY EXCERPT -->
		<div class="full" style="margin-bottom: 10px;">
			<div class="half">
				<label class="customlabel" for="<?php echo $this->get_field_id('display_excerpt'); ?>">
					<?php _e('Display Excerpt:', 'bluth_admin'); ?>
					<small><?php _e('Do you want to display the excerpt?', 'bluth_admin'); ?></small>
				</label>
			</div>
			<div class="half">
				<select class="normal" id="<?php echo $this->get_field_id('display_excerpt'); ?>" name="<?php echo $this->get_field_name('display_excerpt'); ?>">
					<option value="true"<?php selected($instance['display_excerpt'], 'true') ?>>On</option>
					<option value="false"<?php selected($instance['display_excerpt'], 'false') ?>>Off</option>
				</select>
			</div>
		</div>
		<!-- DISPLAY AUTHOR -->
		<div class="full" style="margin-bottom: 10px;">
			<div class="half">
				<label class="customlabel" for="<?php echo $this->get_field_id('display_author'); ?>">
					<?php _e('Display Author:', 'bluth_admin'); ?>
					<small><?php _e('Do you want to display the author in the footer?', 'bluth_admin'); ?></small>
				</label>
			</div>
			<div class="half">
				<select class="normal" id="<?php echo $this->get_field_id('display_author'); ?>" name="<?php echo $this->get_field_name('display_author'); ?>">
					<option value="true"<?php selected($instance['display_author'], 'true') ?>>On</option>
					<option value="false"<?php selected($instance['display_author'], 'false') ?>>Off</option>
				</select>
			</div>
		</div>
		<!-- DISPLAY DATE -->
		<div class="full" style="margin-bottom: 10px;">
			<div class="half">
				<label class="customlabel" for="<?php echo $this->get_field_id('display_date'); ?>">
					<?php _e('Display Date:', 'bluth_admin'); ?>
					<small><?php _e('Do you want to display the date in the footer?', 'bluth_admin'); ?></small>
				</label>
			</div>
			<div class="half">
				<select class="normal" id="<?php echo $this->get_field_id('display_date'); ?>" name="<?php echo $this->get_field_name('display_date'); ?>">
					<option value="true"<?php selected($instance['display_date'], 'true') ?>>On</option>
					<option value="false"<?php selected($instance['display_date'], 'false') ?>>Off</option>
				</select>
			</div>
		</div>
		<!-- DISPLAY DUPLICATES -->
		<div class="full" style="margin-bottom: 10px;">
			<div class="half">
				<label class="customlabel" for="<?php echo $this->get_field_id('display_duplicates'); ?>">
					<?php _e('Display Duplicates:', 'bluth_admin'); ?>
					<small><?php _e('Do you want to display posts even though they\'ve already been displayed?', 'bluth_admin'); ?></small>
				</label>
			</div>
			<div class="half">
				<select class="normal" id="<?php echo $this->get_field_id('display_duplicates'); ?>" name="<?php echo $this->get_field_name('display_duplicates'); ?>">
					<option value="true"<?php selected($instance['display_duplicates'], 'true') ?>>On</option>
					<option value="false"<?php selected($instance['display_duplicates'], 'false') ?>>Off</option>
				</select>
			</div>
		</div>
		<!-- DISPLAY LOAD MORE BUTTON -->
		<div class="full" style="margin-bottom: 10px;">
			<div class="half">
				<label class="customlabel" for="<?php echo $this->get_field_id('loadmorebutton'); ?>">
					<?php _e('Load More Button:', 'bluth_admin'); ?>
					<small><?php _e('Display load more button?', 'bluth_admin'); ?></small>
				</label>
			</div>
			<div class="half">
				<select class="normal" id="<?php echo $this->get_field_id('loadmorebutton'); ?>" name="<?php echo $this->get_field_name('loadmorebutton'); ?>">
					<option value="true"<?php selected($instance['loadmorebutton'], 'true') ?>>On</option>
					<option value="false"<?php selected($instance['loadmorebutton'], 'false') ?>>Off</option>
				</select>
			</div>
		</div>
		<div class="full" style="margin-bottom: 10px;">

			<!-- CATEGORIES -->
			<!-- CATEGORIES -->
			<!-- CATEGORIES -->
			<div class="half">
				<label class="customlabel" for="<?php echo $this->get_field_id('cat_posts'); ?>">
					<?php _e('Only Show Categories:', 'bluth_admin'); ?>
					<small><?php _e('Hold CTRL for multiple', 'bluth_admin'); ?></small>
				</label>
				<select style="min-height: 108px;" class="normal" id="<?php echo $this->get_field_id('cat_posts'); ?>" name="<?php echo $this->get_field_name('cat_posts'); ?>[]" multiple><?php 
					if(is_array($cat_posts) and in_array('0', $cat_posts)){ ?>
						<option value="0" selected>All</option><?php 
					}else{ ?>
						<option value="0">All</option><?php
					} 
					// $category_ids = get_all_category_ids();
					$categories = get_categories( array( 'hide_empty' => 1 ) );
					foreach($categories as $category) {
					 	echo (is_array($cat_posts) and in_array($category->term_id, $cat_posts)) ? '<option value="'.(int)$category->term_id.'" selected>'.$category->name.'</option>' : '<option value="'.$category->term_id.'">'.$category->name.'</option>';
					} ?>
				</select>
			</div>
			<div class="half">

				<!-- TAGS -->
				<!-- TAGS -->
				<!-- TAGS -->
				<div class="full" style="margin-bottom: 10px;">
					
					<label class="customlabel" for="<?php echo $this->get_field_id('tag_posts'); ?>">
						<?php _e('Only Show Tags', 'bluth_admin'); ?>
						<small><?php _e('Separated by comma', 'bluth_admin'); ?></small>
					</label><?php

				   	  	$tags = $tag_posts;

				   	  	if(is_array($tag_posts)){
				   	  		$tags = array();
				   	  		foreach ($tag_posts as $tag) {
				   	  			$tag_obj = get_tag($tag);
				   	  			if($tag_obj){
				   	  				$tags[] = $tag_obj->name;
				   	  			}
				   	  		}
							$tags = implode(',', $tags);
				   	  	}				
				   	?>
					<input class="normal" id="<?php echo $this->get_field_id('tag_posts'); ?>" name="<?php echo $this->get_field_name('tag_posts'); ?>" type="text" value="<?php echo esc_attr($tags); ?>" onfocus="setSuggestTags('<?php echo $this->get_field_id('tag_posts'); ?>');" />
				</div>

				<div class="full" style="margin-bottom: 10px;">	

					<!-- ORDER BY -->
					<!-- ORDER BY -->
					<!-- ORDER BY -->
					<label class="customlabel" for="<?php echo $this->get_field_id('orderby'); ?>">
						<?php _e('Order By:', 'bluth_admin'); ?>
						<small><?php _e('How to order the posts', 'bluth_admin'); ?></small>
					</label>
					<select class="normal" id="<?php echo $this->get_field_id('orderby'); ?>" name="<?php echo $this->get_field_name('orderby'); ?>">
						<option value="date"<?php echo $orderby == 'date' ? ' selected' : ''; ?>>Date</option>
						<option value="name"<?php echo $orderby == 'name' ? ' selected' : ''; ?>>Name</option>
					</select>
				</div>
				<div class="full" style="margin-bottom: 10px;">	

					<!-- ORDER -->
					<!-- ORDER -->
					<!-- ORDER -->
					<label class="customlabel" for="<?php echo $this->get_field_id('order'); ?>">
						<?php _e('Order Sort:', 'bluth_admin'); ?>
						<small><?php _e('How to order the posts', 'bluth_admin'); ?></small>
					</label>
					<select class="normal" id="<?php echo $this->get_field_id('order'); ?>" name="<?php echo $this->get_field_name('order'); ?>">
						<option value="desc"<?php echo $order == 'desc' ? ' selected' : ''; ?>>Descending</option>
						<option value="asc"<?php echo $order == 'asc' ? ' selected' : ''; ?>>Ascending</option>
					</select>
				</div>	
			</div>
		</div>
		<br>
		<hr>
		<br>

		<script type="text/javascript" >
		    // Function to add auto suggest
		    if (typeof(setSuggestTags) !== "function") { 
			    function setSuggestTags(id) {
			        jQuery('#' + id).suggest("<?php echo site_url(); ?>/wp-admin/admin-ajax.php?action=ajax-tag-search&tax=post_tag", {multiple:true, multipleSep: ","});
			    }
		    }
	    </script>

<?php
	}
}
add_action( 'widgets_init', create_function('', 'return register_widget("bl_posts");') );