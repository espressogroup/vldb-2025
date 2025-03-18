# Exploring the Performance of Scalable Keyword Search on Decentralized Data with Differential Visibility Constraints

## Overview

This repository contains the source code, data, and supplementary materials for our paper submitted to the **VLDB 2025 Conference**. Our work investigates scalable keyword search and querying in **decentralized data cooperatives** with differential data access. We focus on:

- **Decentralized indexing mechanisms** that respect differential visibility constraints.
- **Metadata-driven source selection** for query efficiency and privacy.
- **Decentralized ranking techniques** to optimize search results.
- **Experimental evaluation** in a healthcare-inspired decentralized personal data setting.

## Key Contributions

- **Decentralized Search Framework**: We design a privacy-aware, decentralized keyword search mechanism using metadata-driven query processing.
- **Differential Access Control**: The system ensures that search results respect access permissions set by data store owners.
- **Scalability and Efficiency**: Our experiments demonstrate that **metadata-driven search significantly reduces execution time while maintaining retrieval quality comparable to centralized methods**.
- **Real-World Inspired Deployment**: We test our approach in a **federated healthcare data network**, evaluating performance trade-offs between efficiency, privacy, and scalability.

## Experimental Setup

Our experiments simulate a **decentralized federated health data network**, where patients' medical records are stored in **Personal Online Data Stores (Pods)** hosted on **Solid servers**. The experimental configuration includes:

- **50 Solid servers**, each hosting **9,500 pods** (~475,000 pods in total).
- **Synthetic healthcare dataset** generated using **Synthea**.
- **Differential access control** assigned to search parties (ranging from 5% to 100% access levels).
- **Search engine** implemented using **Apache Lucene**.
- **Decentralized ranking mechanism** leveraging **BM25 and metadata-aware ranking**.

## Implementation Details

- **Decentralized Indexing**: Each pod maintains **search-party-specific indexes**, ensuring visibility constraints are respected.
- **Metadata for Source Selection**:
  - **System-Level Metadata**: Helps in selecting relevant servers.
  - **Server-Level Metadata**: Helps in selecting relevant pods.
  - **Bloom Filters**: Used for privacy-preserving metadata disclosure.
- **Query Execution Process**:
  1. Selecting relevant servers based on **system-level metadata**.
  2. Selecting relevant pods within each server based on **server-level metadata**.
  3. Fetching results from indexed personal data stores (pods).
  4. Aggregating and ranking results based on **BM25 scoring**.

## Results Summary

- **Metadata-driven search reduces execution time** by a factor of **4.3× to 24.0×**, depending on access levels.
- **Selective metadata disclosure (Bloom Filters)** balances privacy with search efficiency.
- **Decentralized ranking ensures retrieval quality comparable to centralized search**.

## Repository Structure

