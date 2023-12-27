package de.ritterbach.jameica.energie.gui.views;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Auswertung implements Serializable {
	private Date datum;
	private Long anzahlTage;
	private BigDecimal grundpreisAnteil;
	private BigDecimal arbeitspreis;
	private BigDecimal gezahlt;
	private BigDecimal genutzt;
	private BigDecimal summe;
	private String notiz;

	public Auswertung() {
		super();
	}

	public Auswertung(Date datum, BigDecimal summe, String notiz) {
		this.datum = datum;
		this.summe = summe;
		this.notiz = notiz;
	}

	public Auswertung(Date datum, Long anzahlTage, BigDecimal anteilGrund, BigDecimal summe, String notiz) {
		this.datum = datum;
		this.anzahlTage = anzahlTage;
		this.grundpreisAnteil = anteilGrund;
		this.summe = summe;
		this.notiz = notiz;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public Long getAnzahlTage() {
		return anzahlTage;
	}

	public void setAnzahlTage(long anzahlTage) {
		this.anzahlTage = anzahlTage;
	}

	public BigDecimal getGrundpreisAnteil() {
		return grundpreisAnteil;
	}

	public void setGrundpreisAnteil(BigDecimal grundpreisAnteil) {
		this.grundpreisAnteil = grundpreisAnteil.negate();
	}

	public BigDecimal getArbeitspreis() {
		return arbeitspreis;
	}

	public void setArbeitspreis(BigDecimal arbeitspreis) {
		this.arbeitspreis = arbeitspreis.negate();
	}

	public BigDecimal getGezahlt() {
		return gezahlt;
	}

	public void setGezahlt(BigDecimal gezahlt) {
		this.gezahlt = gezahlt;
	}

	public BigDecimal getGenutzt() {
		return genutzt;
	}

	public void setGenutzt(BigDecimal genutzt) {
		this.genutzt = genutzt.negate();
	}

	public BigDecimal getSumme() {
		return summe;
	}

	public void setSumme(BigDecimal summe) {
		this.summe = summe;
	}

	public String getNotiz() {
		return notiz;
	}

	public void setNotiz(String notiz) {
		this.notiz = notiz;
	}

}