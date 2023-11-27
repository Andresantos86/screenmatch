package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverterDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
  Scanner leitura = new Scanner(System.in);
  private final String ENDERECO = "https://www.omdbapi.com/?t=";
  private final String API_KEY = System.getenv("API_KEY");

  private ConsumoApi consumo = new ConsumoApi();

  private ConverterDados conversor = new ConverterDados();

  private List<DadosSerie> dadosSerie = new ArrayList<>();
  private List<Serie> series = new ArrayList<>();
  private SerieRepository serieRepository;

  public Principal (SerieRepository serieRepository){
    this.serieRepository = serieRepository;
  }


  public void exibeMenu (){
    var opcao = -1;
    while (opcao != 0) {
      var menu = """
              1 - Buscar séries
              2 - Buscar episódios
              3 - Séries buscadas    
              4 - Buscar série por titulo   
              5 - Listar série por ator
              6 - Top 5 series  
              7 - Buscar por categoria      
              0 - Sair                                 
              """;


      System.out.println(menu);
      opcao = leitura.nextInt();
      leitura.nextLine();

      switch (opcao) {
        case 1:
          buscarSerieWeb();
          break;
        case 2:
          buscarEpisodioPorSerie();
          break;
        case 3:
          listarSeriesBuscadas();
          break;
        case 4:
          listarSeriePorTitulo();
          break;
        case 5:
          listarSeriePorAtor();
          break;
        case 6:
          buscarTopSeries();
          break;
        case 7:
          buscaPorCategoria();
          break;
        case 0:
          System.out.println("Saindo...");
          break;
        default:
          System.out.println("Opção inválida");
      }
    }
  }



  private void buscarSerieWeb() {
    DadosSerie dados = getDadosSerie();
    Serie serie = new Serie(dados);
    serieRepository.save(serie);
    System.out.println(dados);
  }

  private DadosSerie getDadosSerie() {
    System.out.println("Digite o nome da série para busca");
    var nomeSerie = leitura.nextLine();
    var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
    DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
    return dados;
  }
  private void buscarEpisodioPorSerie(){
    listarSeriesBuscadas();
    System.out.println("Digite o nome da série para buscar episódios");
    var nomeSerie = leitura.nextLine();
    // isso pode ser substituido por findByTituloContainsIgnoreCase do repository
    Optional<Serie> serie = series.stream().filter(s-> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
            .findFirst();
    if(serie.isPresent()){
      var serieEncontrada = serie.get();
      List<DadosTemporada> temporadas = new ArrayList<>();

      for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
        var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
        DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
        temporadas.add(dadosTemporada);
      }
      temporadas.forEach(System.out::println);
      List<Episodio> episodios = temporadas.stream().flatMap(t-> t.episodios().stream()
              .map(e-> new Episodio(t.temporada(), e)))
              .collect(Collectors.toList());
      serieEncontrada.setEpisodios(episodios);
      serieRepository.save(serieEncontrada);
    }else{
      System.out.println("Serie não encontrada");
    }


  }
  @Deprecated
  private void buscarEpisodioPorSerie2(){
    DadosSerie dadosSerie = getDadosSerie();
    List<DadosTemporada> temporadas = new ArrayList<>();

    for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
      var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
      DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
      temporadas.add(dadosTemporada);
    }
    temporadas.forEach(System.out::println);
  }

  private void listarSeriesBuscadas (){
    series = serieRepository.findAll();
    series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);
  }

  private void listarSeriePorTitulo() {
    System.out.println("Escolha uma serie por nome");
    var nomeSerie = leitura.nextLine();
    Optional<Serie> serieBuscada = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

    if(serieBuscada.isPresent()){
      System.out.println("Dados da serie: "+serieBuscada.get());
    }else{
      System.out.println("Série não encontrada");
    }
  }

  private  void listarSeriePorAtor(){
    System.out.println("Escolha a serie por ator");
    var nomeAtor = leitura.nextLine();
    Optional<Serie> seriePorAtor = serieRepository.findByAtoresContainingIgnoreCase(nomeAtor);

    if (seriePorAtor.isPresent()){
      System.out.println("Dados da serie: "+seriePorAtor.get());
    }else{
      System.out.println("Série não encontrada");
    }
  }
  private void buscarTopSeries(){
    List<Serie>serieTop = serieRepository.findTop5ByOrderByAvaliacaoDesc();
    serieTop.forEach(s-> System.out.println(s.getTitulo()+" avaliação: "+s.getAvaliacao()));
  }

  private void buscaPorCategoria(){
    System.out.println("Buscar serie por categoria / genero ");
    var nomeCategoria = leitura.nextLine();
    Categoria categoria = Categoria.fromPortugues(nomeCategoria);
    List<Serie> seriesCategoria = serieRepository.findByGenero(categoria);
    if(seriesCategoria.isEmpty()){
      System.out.println("Não há series para a categoria escolhida");
    }else{
      System.out.println("Séries da categoria: "+nomeCategoria);
      seriesCategoria.forEach(System.out::println);
    }
  }
}
