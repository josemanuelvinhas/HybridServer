# HybridServer
Servidor Híbrido de Documentos Estructurados desarrollado en la asignatura DAI (Desarrollo de Aplicaciones para Internet).

Este proyecto consistirá en el desarrollo de un sistema híbrido cliente/servidor y P2P para la gestión de documentos estructurados (HTML y XML)

### Base de datos

Se usa el SGBD _MariaDB 10.4.14_ de [XAMPP](https://www.apachefriends.org/). A través de _phpMyAdmin_ se puede crear la base de datos usando el siguiente script:

```
DROP DATABASE IF EXISTS hstestdb;

CREATE DATABASE hstestdb CHARACTER SET utf8mb4;

USE hstestdb;

CREATE TABLE HTML (
  uuid char(36) PRIMARY KEY NOT NULL,
  content text DEFAULT NULL
);

CREATE TABLE XML (
  uuid char(36) PRIMARY KEY NOT NULL,
  content text DEFAULT NULL
);

CREATE TABLE XSD (
  uuid char(36) PRIMARY KEY NOT NULL,
  content text DEFAULT NULL
);

CREATE TABLE XSLT (
  uuid char(36) PRIMARY KEY NOT NULL,
  content text DEFAULT NULL,
  xsd char(36) DEFAULT NULL
);


grant all privileges on hstestdb.* to hsdb@localhost identified by "hsdbpass";
```


## Construido con :hammer_and_wrench:

* [Eclipse IDE for Enterprise Java Developers](https://www.eclipse.org/downloads/packages/release/2020-06/r)

## Autores :black_nib:

* **José Manuel Viñas Cid** -  [josemanuelvinhas](https://github.com/josemanuelvinhas)
* **Yomar Costa Orellana** - [Yomiquesh](https://github.com/Yomiquesh)
