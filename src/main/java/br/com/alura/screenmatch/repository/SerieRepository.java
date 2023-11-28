package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SerieRepository extends JpaRepository<Serie,Long> {
  Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

  Optional<Serie> findByAtoresContainingIgnoreCase(String nomeAtor);
  List<Serie> findTop5ByOrderByAvaliacaoDesc();

  List<Serie> findByGenero(Categoria categoria);
  //SQL = select * from series where series.total_temporadas <= 5 and series.avaliacao >= 7.5
  //Mudando para JPQL inves de usar a tabela usa a classe ,  os : sao parametros do metodo ex:
  @Query("select s from Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
  List<Serie> seriesPorTemporadaEAvaliacao (int totalTemporadas, double avaliacao);
  @Query("select e from Serie s join s.episodios e where e.titulo ILIKE %:nomeEpisodio%")
  List<Episodio> episodioPorTrecho(String nomeEpisodio);
  @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
  List<Episodio> topEpisodiosPorSerie(Serie serie);
  @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLancamento")
  List<Episodio> episodiosPorSerieEAno(Serie serie, int anoLancamento);
}
