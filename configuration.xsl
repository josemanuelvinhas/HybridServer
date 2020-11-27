<?xml version="1.0" encoding="UTF-8"?> <!-- Revisar css y js -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:cfg="http://www.esei.uvigo.es/dai/hybridserver"
	xsi:schemaLocation="http://www.esei.uvigo.es/dai/hybridserver configuration.xsd">

	<xsl:output method="html" encoding="utf-8" indent="yes" />
	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
		<html lang="en">
			<head>
				<title>HybridServer Configuration</title>
				<link rel="stylesheet"
					href="https://code.jquery.com/ui/1.12.1/themes/cupertino/jquery-ui.css" />
				<script
					src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
				<script
					src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
				<script>
					$(function() {
					$("#servers").accordion({
					autoHeight: false,
					collapsible: true
					});
					});
				</script>
				<style>
					body {
					font-family: arial, sans-serif;
					font-size: 0.8em;
					}
					h1 {
					text-shadow: 1px 1px 1px #999;
					}

					div#container {
					margin: 0 auto;
					max-width: 960px;
					min-width: 200px;
					}
				</style>
			</head>
			<body>
				<div id="container">
					<h1>HybridServer Configuration</h1>
					<div>
						<h3>Connections</h3>
						<ol>
							<li>
								<strong>HTTP: </strong>
								<xsl:value-of
									select="cfg:configuration/cfg:connections/cfg:http" />
							</li>
							<li>
								<strong>Webservice: </strong>
								<xsl:value-of
									select="cfg:configuration/cfg:connections/cfg:webservice" />
							</li>
							<li>
								<strong>Numclients: </strong>
								<xsl:value-of
									select="cfg:configuration/cfg:connections/cfg:numClients" />
							</li>
						</ol>
					</div>
					<div>
						<h3>Database</h3>
						<ol>
							<li>
								<strong>User: </strong>
								<xsl:value-of
									select="cfg:configuration/cfg:database/cfg:user"></xsl:value-of>
							</li>
							<li>
								<strong>Password: </strong>
								<xsl:value-of
									select="cfg:configuration/cfg:database/cfg:password"></xsl:value-of>
							</li>
							<li>
								<strong>URL: </strong>
								<xsl:value-of
									select="cfg:configuration/cfg:database/cfg:url"></xsl:value-of>
							</li>
						</ol>
					</div>
					<div id="servers">
						<xsl:apply-templates
							select="cfg:configuration/cfg:servers/cfg:server" />
					</div>
					<p>Authors: Yomar Costa Orellana &amp; José Manuel Viñas Cid</p>
				</div>
				
			</body>
		</html>
	</xsl:template>

	<xsl:template match="cfg:server">
		<h3>
			<xsl:value-of select="@name" />
		</h3>
		<div>
			<div>
				<div>
					<strong>WSDL: </strong>
					<xsl:value-of select="@wsdl"></xsl:value-of>
				</div>
				<div>
					<strong>Namespace: </strong>
					<xsl:value-of select="@namespace"></xsl:value-of>
				</div>
				<div>
					<strong>Service: </strong>
					<xsl:value-of select="@service"></xsl:value-of>
				</div>
				<div>
					<strong>HTTP Address: </strong>
					<xsl:value-of select="@httpAddress"></xsl:value-of>
				</div>
			</div>
		</div>

	</xsl:template>
</xsl:stylesheet>