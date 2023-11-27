package br.com.alura.screenmatch.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ManyToAny;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "episodios")
public class Episodio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Integer temporada;
  private String titulo;
  private Integer numeroEpisodio;
  private Double avaliacao;
  private LocalDate dataLancamento;
  @ManyToOne()
  @JoinColumn(name = "id_serie")
  private Serie serie;

  public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodio) {
    this.temporada = numeroTemporada;
    this.titulo = dadosEpisodio.Titulo();
    this.numeroEpisodio = dadosEpisodio.episodio();
    try {
      this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
    } catch (NumberFormatException ex) {
      this.avaliacao = 0.0;
    }
    try {
      this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
    } catch (DateTimeParseException ex) {
      this.dataLancamento = null;
    }

  }

  public Episodio() {
  }

  public Integer getTemporada() {
    return temporada;
  }

  public void setTemporada(Integer temporada) {
    this.temporada = temporada;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public Integer getNumeroEpisodio() {
    return numeroEpisodio;
  }

  public void setNumeroEpisodio(Integer numeroEpisodio) {
    this.numeroEpisodio = numeroEpisodio;
  }

  public Double getAvaliacao() {
    return avaliacao;
  }

  public void setAvaliacao(Double avaliacao) {
    this.avaliacao = avaliacao;
  }

  public LocalDate getDataLancamento() {
    return dataLancamento;
  }

  public void setDataLancamento(LocalDate dataLancamento) {
    this.dataLancamento = dataLancamento;
  }

  public Serie getSerie() {
    return serie;
  }

  public void setSerie(Serie serie) {
    this.serie = serie;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "{" +
            "temporada=" + temporada +
            ", titulo='" + titulo + '\'' +
            ", numeroEpisodio=" + numeroEpisodio +
            ", avaliacao=" + avaliacao +
            ", dataLancamento=" + dataLancamento;
  }
}
