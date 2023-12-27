-- ----------------------------------------------------------------------
-- Erweitert die Tabellen "kosten" um die Spalte "abschlag_bis"
-- Diese bekommt die Werte aus gueltig_bis
-- ----------------------------------------------------------------------

ALTER TABLE kosten_gas    ADD abschlag_bis date;
ALTER TABLE kosten_strom  ADD abschlag_bis date;
ALTER TABLE kosten_wasser ADD abschlag_bis date;
UPDATE kosten_gas    SET abschlag_bis = gueltig_bis;
UPDATE kosten_strom  SET abschlag_bis = gueltig_bis;
UPDATE kosten_wasser SET abschlag_bis = gueltig_bis;

-- ----------------------------------------------------------------------
-- Benennt die Spalte "kosten" um die Spalte "rechnungsabschluss" um in 
-- "neue_periode"
-- ----------------------------------------------------------------------
ALTER TABLE kosten_gas    RENAME COLUMN rechnungsabschluss TO neue_periode;
ALTER TABLE kosten_strom  RENAME COLUMN rechnungsabschluss TO neue_periode;
ALTER TABLE kosten_wasser RENAME COLUMN rechnungsabschluss TO neue_periode;
