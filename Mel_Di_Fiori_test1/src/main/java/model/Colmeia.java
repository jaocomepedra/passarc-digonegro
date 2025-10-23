package model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "colmeias")
public class Colmeia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @Column(nullable = false) // campo obrigatório (NOT NULL no banco)
    private String identificacao;

    @Column(nullable = false)
    private String localizacao;

    @Column(nullable = false)
	private String tipo;

    @Column(nullable = false)
	private String status;

    @Column(nullable = false)
	private LocalDate dataInstalacao;

	private int numeroQuadros;

	@Column(length = 2000)
    private String observacoes;




    // 🔄 Construtor vazio exigido pelo JPA
	public Colmeia() {
	}

	 // ✅ Construtor com todos os atributos
	public Colmeia(String identificacao, String localizacao, String tipo, String status, LocalDate dataInstalacao,
			int numeroQuadros, String observacoes) {
		this.identificacao = identificacao;
		this.localizacao = localizacao;
		this.tipo = tipo;
		this.status = status;
		this.dataInstalacao = dataInstalacao;
		this.numeroQuadros = numeroQuadros;
		this.observacoes = observacoes;
	}

    // ➕ Getters e Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdentificacao() {
		return identificacao;
	}

	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getDataInstalacao() {
		return dataInstalacao;
	}

	public void setDataInstalacao(LocalDate dataInstalacao) {
		this.dataInstalacao = dataInstalacao;
	}

	public int getNumeroQuadros() {
		return numeroQuadros;
	}

	public void setNumeroQuadros(int numeroQuadros) {
		this.numeroQuadros = numeroQuadros;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	@Override
	public String toString() {
		return "Planta [id=" + id + ", identificacao=" + identificacao + ", localizacao=" + localizacao + ", tipo="
				+ tipo + ", status=" + status + ", dataInstalacao=" + dataInstalacao + ", numeroQuadros="
				+ numeroQuadros + ", observacoes=" + observacoes + "]";
	}

}
