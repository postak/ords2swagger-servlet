# Exposing Oracle REST Data Services using Swagger

This Servlet generates a swagger YAML from Oracle REST Data Services configuration created either via ORDS or via APEX

### Building ###

Use maven to generate the war file

	$ mvn clean install

As this software use Oracle jdbc drivers from Oracle maven repository, you need to configure the maven setting.xml following the instructions available [here](https://blogs.oracle.com/dev2dev/entry/how_to_get_oracle_jdbc#settings)


