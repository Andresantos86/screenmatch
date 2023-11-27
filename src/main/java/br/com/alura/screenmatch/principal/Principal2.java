package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverterDados;

import java.util.*;
import java.util.stream.Collectors;



public class Principal2 {
  Scanner leitura = new Scanner(System.in);
  private final String ENDERECO = "https://www.omdbapi.com/?t=";
  private final String API_KEY = System.getenv("API_KEY");

  private ConsumoApi consumo = new ConsumoApi();
  private ConverterDados conversor = new ConverterDados();

  private List<DadosSerie> dadosSerie = new ArrayList<>();
  //Usado na primeira versao
  @Deprecated
  public void exibeMenu1() {

    System.out.println("Digite o nome da série para a busca");
    var nomeSerie = leitura.nextLine();

    var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
    DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
    System.out.println(dados);

    List<DadosTemporada> temporadas = new ArrayList<>();

    for (int i = 1; i <= dados.totalTemporadas(); i++) {
      json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
      DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
      temporadas.add(dadosTemporada);

    }
    temporadas.forEach(System.out::println);

    //imprime so os titulos equivale a laço for encadeado

    temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.Titulo())));

    // Pega todos os dados de todas as temporadas e coloca em uma nova lista
    List<DadosEpisodio> dadosEpisodios = temporadas.stream()
            .flatMap(t -> t.episodios().stream()).collect(Collectors.toList());
    // metodo toList vira lista imutavel por isso usa o colletc

    //pegar os dados ordenados por avaliacao limitado em 5 ignora sem avaliação
    System.out.println("\nTop 5 Episódios");

    dadosEpisodios.stream()
            .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
            .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
            .limit(5).forEach(System.out::println);

    // lista convertendo dadosEpisodio para episodio
    List<Episodio> episodios = temporadas.stream()
            .flatMap(t -> t.episodios().stream()
                    .map(d -> new Episodio(t.temporada(), d)))
            .collect(Collectors.toList());

//    episodios.forEach(System.out::println);
//
//    System.out.println("A partir de que ano você quer ver os episodios?");
//    var ano = sc.nextInt();
//    sc.nextLine();
//
//    LocalDate dataBusca = LocalDate.of(ano,1,1);
//    DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//     episodios.stream()
//            .filter(e-> e.getDataLancamento()!= null && e.getDataLancamento().isAfter(dataBusca))
//            .forEach(e-> System.out.println(
//                    " Temporada: " + e.getTemporada()+ " Episódio : " + e.getTitulo()
//                    + " Data lançameto : " + e.getDataLancamento().format(formatador)));


    // excontrar por nome de episodio.
//    System.out.println("Digite o titulo de episodio ou parte dele para localizar a temporada");
//    var trechoTitulo = sc.nextLine();
//    Optional<Episodio> busca = episodios.stream()
//            .filter(e-> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase())).findFirst();
//
//    if(busca.isPresent()){
//      System.out.println("Episodio encontrado!");
//      System.out.println("Temporada : "+ busca.get());
//    }else{
//      System.out.println("Desculpe não foi possivel encontrar o episódio");
//    }
    // Map de avaliações por temporeada
    Map<Integer,Double> avTemporada = episodios.stream()
            .filter(e-> e.getAvaliacao() > 0.0)
            .collect(Collectors.groupingBy(Episodio::getTemporada,
                    Collectors.averagingDouble(Episodio::getAvaliacao)));

    System.out.println(avTemporada);

    // usando Lambda para estatisticas.
    DoubleSummaryStatistics est = episodios.stream()
            .filter(e-> e.getAvaliacao() > 0.0)
            .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
    System.out.println("Média: "+est.getAverage());
    System.out.println("Melhor avaliação: "+ est.getMax());
    System.out.println("Pior avaliação: " + est.getMin());
    System.out.println("Episódios avaliados: "+ est .getCount());
  }
  // Fim primeira versao de menu
}
