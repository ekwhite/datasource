package edu.ucdenver.ccp.datasource.fileparsers.pharmgkb;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2015 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */


import java.util.Collection;
import java.util.Set;

import lombok.Data;

import org.apache.log4j.Logger;

import edu.ucdenver.ccp.datasource.fileparsers.License;
import edu.ucdenver.ccp.datasource.fileparsers.Record;
import edu.ucdenver.ccp.datasource.fileparsers.RecordField;
import edu.ucdenver.ccp.datasource.fileparsers.SingleLineFileRecord;
import edu.ucdenver.ccp.datasource.identifiers.DataSource;
import edu.ucdenver.ccp.datasource.identifiers.DataSourceIdentifier;
import edu.ucdenver.ccp.datasource.identifiers.ensembl.EnsemblGeneID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.EntrezGeneID;
import edu.ucdenver.ccp.datasource.identifiers.pharmgkb.PharmGkbID;

/**
 * File record capturing single line record from PharmGKB's genes.tsv file.
 * <p>
 * 
 * @author Yuriy Malenkiy
 * 
 */
@Record(dataSource = DataSource.PHARMGKB, schemaVersion = "2", license = License.PHARMGKB, licenseUri = "http://www.pharmgkb.org/download.action?filename=PharmGKB_License.pdf", comment = "data from PharmGKB's genes.tsv file", citation = "M. Whirl-Carrillo, E.M. McDonagh, J. M. Hebert, L. Gong, K. Sangkuhl, C.F. Thorn, R.B. Altman and T.E. Klein. \"Pharmacogenomics Knowledge for Personalized Medicine\" Clinical Pharmacology & Therapeutics (2012) 92(4): 414-417", label = "gene record")
@Data
public class PharmGkbGeneFileRecord extends SingleLineFileRecord {

	private static final Logger logger = Logger.getLogger(PharmGkbGeneFileRecord.class);
	@RecordField
	private final PharmGkbID accessionId;
	@RecordField
	private final Set<EntrezGeneID> entrezGeneIds;
	@RecordField
	private final EnsemblGeneID ensemblGeneId;
	@RecordField
	private final String name;
	@RecordField
	private final String symbol;
	@RecordField
	private final Collection<String> alternativeNames;
	@RecordField
	private final Collection<String> alternativeSymbols;
	@RecordField
	private final boolean isVip;
	@RecordField
	private final boolean hasVariantAnnotation;
	@RecordField(comment = "Note that many of the IDs listed as RefSeq_[something] are not RefSeq IDs. There are GenBank and UniProt IDs mixed in there among possibly others.")
	private final Collection<DataSourceIdentifier<?>> crossReferences;
	@RecordField
	private final boolean hasCpicDosingGuideline;
	@RecordField
	private final String chromosome;
	@RecordField
	private final Integer chromosomalStart;
	@RecordField
	private final Integer chromosomalEnd;

	/**
	 * @param byteOffset
	 * @param lineNumber
	 * @param accessionId
	 * @param entrezGeneId
	 * @param ensemblGeneId
	 * @param name
	 * @param symbol
	 * @param alternativeNames
	 * @param alternativeSymbols
	 * @param isVip
	 * @param hasVariantAnnotation
	 * @param crossReferences
	 */
	public PharmGkbGeneFileRecord(PharmGkbID accessionId, Set<EntrezGeneID> entrezGeneIds, EnsemblGeneID ensemblGeneId,
			String name, String symbol, Collection<String> alternativeNames,
			Collection<String> alternativeSymbols, boolean isVip, boolean hasVariantAnnotation,
			Collection<DataSourceIdentifier<?>> crossReferences, boolean hasCpicDosingGuideline, String chromosome,
			Integer chromosomalStart, Integer chromosomalEnd, long byteOffset, long lineNumber) {
		super(byteOffset, lineNumber);
		this.accessionId = accessionId;
		this.entrezGeneIds = entrezGeneIds;
		this.ensemblGeneId = ensemblGeneId;
		this.name = name;
		this.symbol = symbol;
		this.alternativeNames = alternativeNames;
		this.alternativeSymbols = alternativeSymbols;
		this.isVip = isVip;
		this.hasVariantAnnotation = hasVariantAnnotation;
		this.crossReferences = crossReferences;
		this.hasCpicDosingGuideline = hasCpicDosingGuideline;
		this.chromosome = chromosome;
		this.chromosomalStart = chromosomalStart;
		this.chromosomalEnd = chromosomalEnd;
	}

	public boolean hasVariantAnnotation() {
		return hasVariantAnnotation;
	}

	public boolean hasCpicDosingGuideline() {
		return hasCpicDosingGuideline;
	}

}
