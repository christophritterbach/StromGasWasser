CREATE TABLE zaehler (
      id                serial primary key
    , name              VARCHAR(255) NOT NULL
    , mess_einheit      VARCHAR(10)  NOT NULL
    , ablese_einheit    VARCHAR(10)  NOT NULL
    , nutzt_faktor      smallint NOT NULL
    , ist_aktiv         smallint NOT NULL
    , UNIQUE KEY  (id)
    , UNIQUE KEY  (name)
    , PRIMARY KEY (id)
);

CREATE TABLE kosten (
      id                 serial primary key
    , zaehler_id         int(10) NOT NULL
    , gueltig_von        date NOT NULL
    , gueltig_bis        date NOT NULL
    , abschlag_bis       date
    , grundpreis         DECIMAL(6,2) NOT NULL
    , arbeitspreis       DECIMAL(10,6) NOT NULL
    , faktor             DECIMAL(10,6) NOT NULL
    , abgerechnet        smallint NOT NULL
    , neue_periode       smallint NOT NULL
    , notiz              VARCHAR(1000)
    , CONSTRAINT fk_kosten_zaehler
      FOREIGN KEY (zaehler_id) REFERENCES zaehler (id)
        ON DELETE CASCADE
    , UNIQUE      (id)
    , PRIMARY KEY (id)
);

CREATE TABLE abschlag (
      id                 serial primary key
    , zaehler_id         int(10) NOT NULL
    , abschlag_datum     date NOT NULL
    , abschlag_betrag    DECIMAL(6,2) NOT NULL
    , notiz              VARCHAR(255)
    , CONSTRAINT fk_abschlag_zaehler
      FOREIGN KEY (zaehler_id) REFERENCES zaehler (id)
        ON DELETE CASCADE
    , UNIQUE      (id)
    , PRIMARY KEY (id)
);

CREATE TABLE zaehlerstand (
      id                 serial primary key
    , zaehler_id         int(10) NOT NULL
    , ablese_datum       date NOT NULL
    , ablese_wert        DECIMAL(10)
    , verbrauch          DECIMAL(10)
    , schaetzung         smallint NOT NULL
    , zaehlerwechsel_aus smallint NOT NULL
    , zaehlerwechsel_ein smallint NOT NULL
    , notiz              varchar(255)
    , CONSTRAINT fk_zaehlerstand_zaehler
      FOREIGN KEY (zaehler_id) REFERENCES zaehler (id)
        ON DELETE CASCADE
    , UNIQUE      (id)
    , PRIMARY KEY (id)
);

CREATE TABLE version (
       id serial primary key
     , name varchar(255) NOT NULL
     , version integer NOT NULL
);

CREATE INDEX idx_ab_datum ON abschlag(abschlag_datum);
CREATE INDEX idx_ko_datum ON kosten(gueltig_von);
CREATE INDEX idx_zs_datum ON zaehlerstand(ablese_datum);
CREATE INDEX idx_ab_fk_zaehler ON abschlag(zaehler_id);
CREATE INDEX idx_ko_fk_zaehler ON kosten(zaehler_id);
CREATE INDEX idx_zs_fk_zaehler ON zaehlerstand(zaehler_id);

-- Bevor wir Daten speichern koennen, muessen wir ein COMMIT machen
COMMIT;

INSERT INTO zaehler (name, mess_einheit, ablese_einheit, nutzt_faktor, ist_aktiv) VALUES ('Wasseruhr', 'm³', 'm³', 0, 1);
INSERT INTO zaehler (name, mess_einheit, ablese_einheit, nutzt_faktor, ist_aktiv) VALUES ('Stromzaehler', 'kWh', 'kWh', 0, 1);

INSERT INTO version (name,version) values ('db',1);
