<?cs def:custom_masthead() ?>
	<div id="header">
		<nav class="navbar navbar-default">
			<div class="container-fluid">
				<div class="navbar-header">
				  <a class="navbar-brand" href="<?cs var:toroot ?>../index.html">
					  <img style="width: 50px; max-width:100px; margin-top: -7px;" src="<?cs var:toassets ?>images/logo.png">
					  HALO Android Sdk
				  </a>
				</div>
				<div>
				  <ul class="nav navbar-nav navbar-left">
					<li><a href="https://bitbucket.org/mobgen/halo-android/wiki/Home" target="_blank">Bitbucket project</a></li>
					<li><a href="https://halo.mobgen.com" target="_blank">CMS</a></li>
					<li><a href="mailto:halo@mobgen.com">Contact us</a></li>
				  </ul>
				  <ul class="nav navbar-nav navbar-right">
				  	<li><?cs call:default_search_box() ?></li>
				  </ul>
				</div>
			</div>
			<div id="headerLeft">
				<?cs if:project.name ?>
					<span id="masthead-title"><?cs var:project.name ?></span>
				<?cs /if ?>
			</div>
			<div id="headerRight">
				<?cs if:reference && reference.apilevels ?>
				<?cs call:default_api_filter() ?>
				<?cs /if ?>
			</div>
		</nav>
	</div><!-- header -->
<?cs /def ?>