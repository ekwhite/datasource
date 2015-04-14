/**
 * 
 */
package edu.ucdenver.ccp.rdfizer.rdf.ice;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;

import scala.actors.threadpool.Arrays;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.datasource.fileparsers.FileRecordReader;
import edu.ucdenver.ccp.datasource.fileparsers.ncbi.gene.EntrezGeneInfoFileParser;
import edu.ucdenver.ccp.datasource.identifiers.DataSource;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.taxonomy.NcbiTaxonomyID;
import edu.ucdenver.ccp.fileparsers.dip.DipYYYYMMDDFileParser;
import edu.ucdenver.ccp.fileparsers.drugbank.DrugbankXmlFileRecordReader;
import edu.ucdenver.ccp.fileparsers.ebi.goa.GpAssociationGoaUniprotFileParser;
import edu.ucdenver.ccp.fileparsers.ebi.interpro.InterPro2GoFileParser;
import edu.ucdenver.ccp.fileparsers.ebi.interpro.InterProNamesDatFileParser;
import edu.ucdenver.ccp.fileparsers.ebi.interpro.InterProProtein2IprDatFileParser;
import edu.ucdenver.ccp.fileparsers.ebi.uniprot.SparseTremblXmlFileRecordReader;
import edu.ucdenver.ccp.fileparsers.ebi.uniprot.SwissProtXmlFileRecordReader;
import edu.ucdenver.ccp.fileparsers.ebi.uniprot.UniProtIDMappingFileRecordReader;
import edu.ucdenver.ccp.fileparsers.gad.GeneticAssociationDbAllTxtFileParser;
import edu.ucdenver.ccp.fileparsers.hgnc.HgncDownloadFileParser;
import edu.ucdenver.ccp.fileparsers.hgnc.HgncDownloadFileParser.WithdrawnRecordTreatment;
import edu.ucdenver.ccp.fileparsers.hprd.HprdIdMappingsTxtFileParser;
import edu.ucdenver.ccp.fileparsers.irefweb.IRefWebPsiMitab2_6FileParser;
import edu.ucdenver.ccp.fileparsers.mgi.MGIEntrezGeneFileParser;
import edu.ucdenver.ccp.fileparsers.mgi.MGIPhenoGenoMPFileParser;
import edu.ucdenver.ccp.fileparsers.mgi.MRKListFileParser;
import edu.ucdenver.ccp.fileparsers.mgi.MRKReferenceFileParser;
import edu.ucdenver.ccp.fileparsers.mgi.MRKSequenceFileParser;
import edu.ucdenver.ccp.fileparsers.mgi.MRKSwissProtFileParser;
import edu.ucdenver.ccp.fileparsers.mirbase.MirBaseMiRnaDatFileParser;
import edu.ucdenver.ccp.fileparsers.ncbi.gene.EntrezGene2RefseqFileParser;
import edu.ucdenver.ccp.fileparsers.ncbi.gene.EntrezGeneMim2GeneFileParser;
import edu.ucdenver.ccp.fileparsers.ncbi.gene.EntrezGeneRefSeqUniprotKbCollabFileParser;
import edu.ucdenver.ccp.fileparsers.ncbi.homologene.HomoloGeneDataFileParser;
import edu.ucdenver.ccp.fileparsers.ncbi.omim.OmimTxtFileParser;
import edu.ucdenver.ccp.fileparsers.ncbi.refseq.RefSeqReleaseCatalogFileParser;
import edu.ucdenver.ccp.fileparsers.pharmgkb.PharmGkbDiseaseFileParser;
import edu.ucdenver.ccp.fileparsers.pharmgkb.PharmGkbDrugFileParser;
import edu.ucdenver.ccp.fileparsers.pharmgkb.PharmGkbGeneFileParser;
import edu.ucdenver.ccp.fileparsers.pharmgkb.PharmGkbRelationFileParser;
import edu.ucdenver.ccp.fileparsers.premod.HumanPReModModuleTabFileParser;
import edu.ucdenver.ccp.fileparsers.premod.MousePReModModuleTabFileParser;
import edu.ucdenver.ccp.fileparsers.pro.ProMappingFileParser;
import edu.ucdenver.ccp.fileparsers.reactome.ReactomeUniprot2PathwayStidTxtFileParser;
import edu.ucdenver.ccp.fileparsers.rgd.RgdRatGeneFileRecordReader;
import edu.ucdenver.ccp.fileparsers.rgd.RgdRatGeneMpAnnotationFileRecordReader;
import edu.ucdenver.ccp.fileparsers.rgd.RgdRatGeneNboAnnotationFileRecordReader;
import edu.ucdenver.ccp.fileparsers.rgd.RgdRatGenePwAnnotationFileRecordReader;
import edu.ucdenver.ccp.fileparsers.rgd.RgdRatGeneRdoAnnotationFileRecordReader;
import edu.ucdenver.ccp.fileparsers.transfac.TransfacGeneDatFileParser;
import edu.ucdenver.ccp.fileparsers.transfac.TransfacMatrixDatFileParser;

/**
 * This enum separates RDF generation by data source file. It is intended to provide an easy way to
 * generate RDF for a particular resource. Its methods are aimed at providing a simple manner to
 * generate RDF concurrently in different processes.
 * 
 * @author Center for Computational Pharmacology, UC Denver; ccpsupport@ucdenver.edu
 * 
 */
public enum FileDataSource {

	/**
	 * The DIP data file must be obtained manually. It is assumed to already be in place when RDF
	 * generation commences. It must be the only file in the DIP data source directory.
	 * 
	 */
	DIP(DataSource.DIP) {

		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			logger.info("sourceFileDirectory (exists): (" + sourceFileDirectory.exists() + ")" + sourceFileDirectory);
			logger.info("file listing: " + Arrays.toString(sourceFileDirectory.listFiles()));
			File dipDataFile = sourceFileDirectory.listFiles()[0];
			logger.info("File exists: " + dipDataFile.exists() + " -- " + dipDataFile.getAbsolutePath());
			FileUtil.validateFile(dipDataFile);
			return new DipYYYYMMDDFileParser(dipDataFile, CharacterEncoding.US_ASCII, taxonIds);
		}
	},
	/**
	 * The HPRD HPRD_ID_MAPPINGS.txt file must be obtained manually. It is assumed to already be in
	 * place when RDF generation commences.
	 */
	HPRD_ID_MAPPINGS(DataSource.HPRD) {

		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			File hprdIdMappingFile = new File(sourceFileDirectory,
					HprdIdMappingsTxtFileParser.HPRD_ID_MAPPINGS_TXT_FILE_NAME);
			FileUtil.validateFile(hprdIdMappingFile);
			return new HprdIdMappingsTxtFileParser(hprdIdMappingFile, CharacterEncoding.US_ASCII);
		}
	},
	/**
	 * The TRANSFAC gene.dat and matrix.dat files must be obtained manually. They are assumed to
	 * already be in place when RDF generation commences.
	 */
	TRANSFAC_GENE(DataSource.TRANSFAC) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			File transfacGeneDatFile = new File(sourceFileDirectory, TransfacGeneDatFileParser.GENE_DAT_FILE_NAME);
			FileUtil.validateFile(transfacGeneDatFile);
			return new TransfacGeneDatFileParser(transfacGeneDatFile, CharacterEncoding.ISO_8859_1);
		}
	},

	TRANSFAC_MATRIX(DataSource.TRANSFAC) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			File transfacMatrixDatFile = new File(sourceFileDirectory, TransfacMatrixDatFileParser.MATRIX_DAT_FILE_NAME);
			FileUtil.validateFile(transfacMatrixDatFile);
			return new TransfacMatrixDatFileParser(transfacMatrixDatFile, CharacterEncoding.ISO_8859_1);
		}
	},
	/**
	 * The GAD all.txt data file must be obtained manually. It is assumed to already be in place
	 * when RDF generation commences.
	 */
	GAD(DataSource.GAD) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			File gadAllTxtFile = new File(sourceFileDirectory,
					GeneticAssociationDbAllTxtFileParser.GAD_ALL_TXT_FILE_NAME);
			FileUtil.validateFile(gadAllTxtFile);
			return new GeneticAssociationDbAllTxtFileParser(gadAllTxtFile, CharacterEncoding.US_ASCII);
		}
	},
	/**
	 *
	 */
	PHARMGKB_DISEASE(DataSource.PHARMGKB) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new PharmGkbDiseaseFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},

	PHARMGKB_GENE(DataSource.PHARMGKB) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new PharmGkbGeneFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},

	PHARMGKB_RELATION(DataSource.PHARMGKB) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			File pharmgkbRelationshipsDataFile = new File(sourceFileDirectory, "relationships.tsv");
			return new PharmGkbRelationFileParser(pharmgkbRelationshipsDataFile, CharacterEncoding.UTF_8);
		}
	},

	PHARMGKB_DRUG(DataSource.PHARMGKB) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new PharmGkbDrugFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	/**
	 * 
	 *
	 */
	DRUGBANK(DataSource.DRUGBANK) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new DrugbankXmlFileRecordReader(sourceFileDirectory, cleanSourceFiles);
		}
	},

	/**
	 * 
	 */
	HGNC(DataSource.HGNC) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new HgncDownloadFileParser(sourceFileDirectory, cleanSourceFiles, WithdrawnRecordTreatment.IGNORE);
		}
	},
	/**
	 * 
	 */
	HOMOLOGENE(DataSource.HOMOLOGENE) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new HomoloGeneDataFileParser(sourceFileDirectory, cleanSourceFiles, taxonIds);
		}
	},

	/**
	 * 
	 */
	IREFWEB(DataSource.IREFWEB) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new IRefWebPsiMitab2_6FileParser(sourceFileDirectory, cleanSourceFiles, taxonIds);
		}
	},
	/**
	 * 
	 * 
	 */
	MGI_ENTREZGENE(DataSource.MGI) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new MGIEntrezGeneFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	MGI_MGIPHENOGENO(DataSource.MGI) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new MGIPhenoGenoMPFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	MGI_MRKLIST(DataSource.MGI) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new MRKListFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	MGI_MRKREFERENCE(DataSource.MGI) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new MRKReferenceFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	MGI_MRKSEQUENCE(DataSource.MGI) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new MRKSequenceFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	MGI_MRKSWISSPROT(DataSource.MGI) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new MRKSwissProtFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	MIRBASE(DataSource.MIRBASE) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new MirBaseMiRnaDatFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	/**
	 *
	 */
	OMIM(DataSource.OMIM) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new OmimTxtFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	/**
	 *
	 */
	RGD_GENES(DataSource.RGD) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new RgdRatGeneFileRecordReader(sourceFileDirectory, cleanSourceFiles);
		}
	},

	/**
	 *
	 */
	RGD_GENE_MP(DataSource.RGD) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new RgdRatGeneMpAnnotationFileRecordReader(sourceFileDirectory, cleanSourceFiles);
		}
	},

	/**
	 *
	 */
	RGD_GENE_RDO(DataSource.RGD) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new RgdRatGeneRdoAnnotationFileRecordReader(sourceFileDirectory, cleanSourceFiles);
		}
	},

	/**
	 *
	 */
	RGD_GENE_NBO(DataSource.RGD) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new RgdRatGeneNboAnnotationFileRecordReader(sourceFileDirectory, cleanSourceFiles);
		}
	},

	/**
	 *
	 */
	RGD_GENE_PW(DataSource.RGD) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new RgdRatGenePwAnnotationFileRecordReader(sourceFileDirectory, cleanSourceFiles);
		}
	},

	/**
	 *
	 */
	PREMOD_HUMAN(DataSource.PREMOD) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new HumanPReModModuleTabFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	PREMOD_MOUSE(DataSource.PREMOD) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new MousePReModModuleTabFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},

	/**
	 *
	 */
	PR_MAPPINGFILE(DataSource.PR) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new ProMappingFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	/**
	 *
	 */
	REACTOME_UNIPROT2PATHWAYSTID(DataSource.REACTOME) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new ReactomeUniprot2PathwayStidTxtFileParser(sourceFileDirectory, cleanSourceFiles, idListDir,
					taxonIds);
		}
	},
	/**
	 *
	 */
	REFSEQ_RELEASECATALOG(DataSource.REFSEQ, 3) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new RefSeqReleaseCatalogFileParser(sourceFileDirectory, cleanSourceFiles, taxonIds);
		}
	},
	/**
	 *
	 */
	NCBIGENE_GENE2REFSEQ(DataSource.EG) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new EntrezGene2RefseqFileParser(sourceFileDirectory, cleanSourceFiles, taxonIds);
		}

	},
	NCBIGENE_GENEINFO(DataSource.EG) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new EntrezGeneInfoFileParser(sourceFileDirectory, cleanSourceFiles, taxonIds);
		}

	},
	NCBIGENE_MIM2GENE(DataSource.EG) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new EntrezGeneMim2GeneFileParser(sourceFileDirectory, cleanSourceFiles);
		}

	},
	NCBIGENE_REFSEQUNIPROTCOLLAB(DataSource.EG) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new EntrezGeneRefSeqUniprotKbCollabFileParser(sourceFileDirectory, cleanSourceFiles, idListDir,
					taxonIds);
		}
	},
	/**
	 */
	GOA(DataSource.GOA, 13) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new GpAssociationGoaUniprotFileParser(sourceFileDirectory, cleanSourceFiles, idListDir, taxonIds);
		}
	},
	/**
	 */
	UNIPROT_SWISSPROT(DataSource.UNIPROT) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new SwissProtXmlFileRecordReader(sourceFileDirectory, cleanSourceFiles, taxonIds);
		}
	},
	UNIPROT_IDMAPPING(DataSource.UNIPROT, 3) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new UniProtIDMappingFileRecordReader(sourceFileDirectory, cleanSourceFiles, taxonIds);
		}
	},
	// UNIPROT_TREMBL(DataSource.UNIPROT, 33, 1000000) {
	// @Override
	// protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean
	// cleanSourceFiles,
	// File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
	// return new TremblXmlFileRecordReader(sourceFileDirectory, cleanSourceFiles, taxonIds);
	// }
	// },
	UNIPROT_TREMBL_SPARSE(DataSource.UNIPROT, 33, 1000000) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new SparseTremblXmlFileRecordReader(sourceFileDirectory, cleanSourceFiles,
					taxonIds);
		}
	},

	/**
	 * 
	 */
	INTERPRO_NAMESDAT(DataSource.INTERPRO) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new InterProNamesDatFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	INTERPRO_INTERPRO2GO(DataSource.INTERPRO) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new InterPro2GoFileParser(sourceFileDirectory, cleanSourceFiles);
		}
	},
	INTERPRO_PROTEIN2IPR(DataSource.INTERPRO, 13) {
		@Override
		protected FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
				File idListDir, Set<NcbiTaxonomyID> taxonIds) throws IOException {
			return new InterProProtein2IprDatFileParser(sourceFileDirectory, cleanSourceFiles, idListDir, taxonIds);
		}
	};

	public enum Split {
		BY_STAGES,
		NONE;
	}

	private static final Logger logger = Logger.getLogger(FileDataSource.class);

	/**
	 * For single source files that are processed by multiple stages to produce multiple RDF output
	 * files, the files are processed in blocks of records as indicated by this constant, i.e. 10
	 * million records from the source file will be put into each RDF output file.
	 */
	private static final long BLOCK_RECORD_COUNT = 10000000;
	/**
	 * Constant indicating all stages of RDF generation should be run serially
	 */
	private static final int DO_ALL_STAGES = -1;
	/**
	 * Suffix used for the static rdf files stored on the classpath
	 */
	private static final String STATIC_RDF_FILE_SUFFIX = ".nt";
	/**
	 * 
	 */
	private final DataSource dataSource;

	/**
	 * Indicates the number of individual RDF generation steps for a given resource
	 */
	private final int numberOfStages;

	private final Long blockRecordCount;

	private FileDataSource(DataSource dataSource, int numberOfStages, long blockRecordCount) {
		this.dataSource = dataSource;
		this.numberOfStages = numberOfStages;
		this.blockRecordCount = blockRecordCount;
	}

	private FileDataSource(DataSource dataSource, int numberOfStages) {
		this.dataSource = dataSource;
		this.numberOfStages = numberOfStages;
		this.blockRecordCount = null;
	}

	private FileDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.numberOfStages = 1;
		this.blockRecordCount = null;
	}

	public DataSource dataSource() {
		return dataSource;
	}

	public Long blockRecordCount() {
		return blockRecordCount;
	}

	// /**
	// * @param stageNum
	// * @param baseSourceFileDirectory
	// * @param baseRdfOutputDirectory
	// * @param cleanSourceFiles
	// * @param compress
	// * @param outputRecordLimit
	// * @return a collection of file references to all RDF file that were created
	// * @throws IOException
	// */
	// public Collection<File> runStage(long currentTime, int stageNum, File
	// baseSourceFileDirectory,
	// File baseRdfOutputDirectory, boolean cleanSourceFiles, boolean compress, long
	// outputRecordLimit)
	// throws IOException {
	// logger.info("Running " + this.name() + " Stage " + stageNum);
	// if (stageNum == 0) {
	// initialize(currentTime, baseSourceFileDirectory, baseRdfOutputDirectory);
	// return Collections.emptyList();
	// }
	// File sourceFileDirectory = getSourceFileDirectory(baseSourceFileDirectory);
	// File rdfOutputDirectory = getOutputDirectory(baseRdfOutputDirectory);
	// return generateRdf(sourceFileDirectory, rdfOutputDirectory, currentTime, cleanSourceFiles,
	// compress,
	// outputRecordLimit, stageNum);
	// }
	//
	// /**
	// * Creates all RDF for a given data source by running each stage consecutively.
	// *
	// * @param baseSourceFileDirectory
	// * @param baseRdfOutputDirectory
	// * @param cleanSourceFiles
	// * @param compress
	// * @param outputRecordLimit
	// * @return a collection of file references to all RDF file that were created
	// * @throws IOException
	// */
	// public Collection<File> generateRdf(long currentTime, File baseSourceFileDirectory, File
	// baseRdfOutputDirectory,
	// boolean cleanSourceFiles, boolean compress, long outputRecordLimit) throws IOException {
	// File sourceFileDirectory = getSourceFileDirectory(baseSourceFileDirectory);
	// File rdfOutputDirectory = getOutputDirectory(baseRdfOutputDirectory);
	// initialize(currentTime, baseSourceFileDirectory, baseRdfOutputDirectory);
	// return generateRdf(sourceFileDirectory, rdfOutputDirectory, currentTime, cleanSourceFiles,
	// compress,
	// outputRecordLimit, DO_ALL_STAGES);
	// }

	protected abstract FileRecordReader<?> initFileRecordReader(File sourceFileDirectory, boolean cleanSourceFiles,
			File idListFileDirectory, Set<NcbiTaxonomyID> taxonIds) throws IOException;

	// /**
	// * To be implemented by each DataSourceRdfGenerator instance.
	// *
	// * @param sourceFileDirectory
	// * @param rdfOutputDirectory
	// * @param createdTime
	// * @param cleanSourceFiles
	// * @param compress
	// * @param outputRecordLimit
	// * @param stageNum
	// * @return a collection of file references to all RDF file that were created
	// * @throws IOException
	// */
	// protected abstract Collection<File> generateRdf(File sourceFileDirectory, File
	// rdfOutputDirectory,
	// long createdTime, boolean cleanSourceFiles, boolean compress, long outputRecordLimit) throws
	// IOException;

	// /**
	// * Creates the source file and RDF output directories if necessary
	// *
	// * and copies the data source specific static RDF file from the classpath (the files in
	// * src/main/resources/edu/ucdenver/ccp/rdfizer/rdf/ice) to the RDF output directory
	// *
	// * @param baseSourceFileDirectory
	// * the base directory where source files can be found or are to be stored after
	// * downloading. A datasource-specific subdirectory will be created in this base
	// * directory for each datasource that is processed.
	// * @param baseRdfOutputDirectory
	// * the base directory where RDF output is to be stored. A datasource-specific
	// * subdirectory will be created in this base directory for each datasource that is
	// * processed.
	// * @param cleanSourceFiles
	// * if true the original source files are deleted and then re-downloaded prior to RDF
	// * generation. This parameter is not used in the default case as it's typically
	// * handled in the generateRdf() method, however the value is made available in
	// * initialize() for special cases (See HOMOLOGENE).
	// */
	// public void initialize(long currentTime, File baseSourceFileDirectory, File
	// baseRdfOutputDirectory) {
	// logger.info("Initializing RDF Generator: " + this.name());
	// File outputDirectory = getOutputDirectory(baseRdfOutputDirectory);
	// FileUtil.mkdir(outputDirectory);
	// FileUtil.mkdir(getSourceFileDirectory(baseSourceFileDirectory));
	// // try {
	// // File staticRdfFile = ClassPathUtil.copyClasspathResourceToDirectory(getClass(),
	// // getDataSourceStaticRdfFileName(), outputDirectory);
	// // return outputMetaRdf(RdfNamespace.getNamespace(dataSource), staticRdfFile,
	// // outputDirectory,
	// // createBaseRdfSource(currentTime, baseRdfOutputDirectory));
	// // } catch (IOException e) {
	// // throw new RuntimeException(e);
	// // }
	//
	// }

	// /**
	// * @return the static RDF file name for a specific data source which is the
	// * {@link RdfDataSource#name()} lower-cased with a .nt suffix. This file must
	// * exist on the classpath (src/main/resources/edu/ucdenver/ccp/rdfizer/rdf/ice)
	// */
	// private String getDataSourceStaticRdfFileName() {
	// return this.name().toLowerCase() + STATIC_RDF_FILE_SUFFIX;
	// }

	/**
	 * @param baseRdfOutputDirectory
	 *            the base directory where RDF output is to be stored. A datasource-specific
	 *            subdirectory will be created in this base directory for each datasource that is
	 *            processed.
	 * @return a datasource-specific directory indicating where the RDF files should be created
	 */
	private File getOutputDirectory(File baseRdfOutputDirectory) {
		return new File(baseRdfOutputDirectory, this.name().toLowerCase());
	}

	/**
	 * @param baseSourceFileDirectory
	 *            the base directory where source files can be found or are to be stored after
	 *            downloading. A datasource-specific subdirectory will be created in this base
	 *            directory for each datasource that is processed.
	 * @return a datasource-specific directory indicating where the source data files either are
	 *         available, or where they should be stored when downloaded
	 */
	private File getSourceFileDirectory(File baseSourceFileDirectory) {
		return new File(baseSourceFileDirectory, this.dataSource.name().toLowerCase());
	}

	/**
	 * @return the numberOfStages
	 */
	public int getNumberOfStages() {
		return numberOfStages;
	}

	/**
	 * Counts the number
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int stageCount = 0;
		System.out.println("BY STAGES: ");
		for (FileDataSource source : FileDataSource.values()) {
			for (int i = 0; i < source.getNumberOfStages(); i++) {
				System.out.println("Global Stage: " + (i + 1 + stageCount) + " ==> " + source.name() + " Local Stage: "
						+ (i + 1));
			}
			stageCount += source.getNumberOfStages();
		}
		System.out.println("Total # of stages: " + stageCount + "\n\n");
		stageCount = 0;
		System.out.println("SINGLE STAGE PER SOURCE:");
		for (FileDataSource source : FileDataSource.values()) {
			System.out.println("SGE index: " + (stageCount + 1) + " ==> " + source.name());
			stageCount++;
		}

	}

}
