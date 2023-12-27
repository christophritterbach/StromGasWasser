CREATE TABLE abschlag_wasser (
      id serial primary key
    , abschlag_datum date NOT NULL
    , abschlag_betrag numeric(6,2)  NOT NULL
    , notiz varchar(255)
);

CREATE TABLE abschlag_gas (
      id serial primary key
    , abschlag_datum date NOT NULL
    , abschlag_betrag numeric(6,2)  NOT NULL
    , notiz varchar(255)
);

CREATE TABLE abschlag_strom (
      id serial primary key
    , abschlag_datum date NOT NULL
    , abschlag_betrag numeric(6,2)  NOT NULL
    , notiz varchar(255)
);

CREATE TABLE kosten_wasser (
       id int(10) AUTO_INCREMENT
     , gueltig_von date NOT NULL
     , gueltig_bis date
     , abschlag_bis date
     , grundpreis DECIMAL(6,2) NOT NULL
     , arbeitspreis DECIMAL(10,6) NOT NULL
     , abgerechnet smallint NOT NULL
     , neue_periode smallint NOT NULL
     , notiz VARCHAR(1000)
);

CREATE TABLE kosten_strom (
       id int(10) AUTO_INCREMENT
     , gueltig_von date NOT NULL
     , gueltig_bis date
     , abschlag_bis date
     , grundpreis DECIMAL(6,2) NOT NULL
     , arbeitspreis DECIMAL(10,6) NOT NULL
     , abgerechnet smallint NOT NULL
     , neue_periode smallint NOT NULL
     , notiz VARCHAR(1000)
);

CREATE TABLE kosten_gas (
       id int(10) AUTO_INCREMENT
     , gueltig_von date NOT NULL
     , gueltig_bis date
     , abschlag_bis date
     , grundpreis DECIMAL(6,2) NOT NULL
     , arbeitspreis DECIMAL(10,6) NOT NULL
     , faktor DECIMAL(10,6) NOT NULL
     , abgerechnet smallint NOT NULL
     , neue_periode smallint NOT NULL
     , notiz VARCHAR(1000)
);

CREATE TABLE zaehler_wasser (
       id serial primary key
     , ablese_datum date NOT NULL
     , ablese_wert numeric(10)
     , verbrauch numeric(10)
     , schaetzung smallint NOT NULL
     , zaehlerwechsel_aus smallint NOT NULL
     , zaehlerwechsel_ein smallint NOT NULL
     , notiz varchar(255)
);

CREATE TABLE zaehler_strom (
       id serial primary key
     , ablese_datum date NOT NULL
     , ablese_wert numeric(10)
     , verbrauch numeric(10)
     , schaetzung smallint NOT NULL
     , zaehlerwechsel_aus smallint NOT NULL
     , zaehlerwechsel_ein smallint NOT NULL
     , notiz varchar(255)
);

CREATE TABLE zaehler_gas (
       id serial primary key
     , ablese_datum date NOT NULL
     , ablese_wert numeric(10)
     , verbrauch numeric(10)
     , schaetzung smallint NOT NULL
     , zaehlerwechsel_aus smallint NOT NULL
     , zaehlerwechsel_ein smallint NOT NULL
     , notiz varchar(255)
);

CREATE TABLE version (
       id serial primary key
     , name varchar(255) NOT NULL
     , version integer NOT NULL
);


CREATE INDEX idx_ab_wa_datum ON abschlag_wasser(abschlag_datum);
CREATE INDEX idx_ab_st_datum ON abschlag_strom(abschlag_datum);
CREATE INDEX idx_ab_ga_datum ON abschlag_gas(abschlag_datum);
CREATE INDEX idx_ko_wa_datum ON kosten_wasser(gueltig_von);
CREATE INDEX idx_ko_st_datum ON kosten_strom(gueltig_von);
CREATE INDEX idx_ko_ga_datum ON kosten_gas(gueltig_von);
CREATE INDEX idx_za_wa_datum ON zaehler_wasser(ablese_datum);
CREATE INDEX idx_za_st_datum ON zaehler_strom(ablese_datum);
CREATE INDEX idx_za_ga_datum ON zaehler_gas(ablese_datum);

-- Bevor wir Daten speichern koennen, muessen wir ein COMMIT machen
COMMIT;

INSERT INTO version (name,version) values ('db',2);
