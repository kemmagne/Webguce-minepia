package org.guce.web.process.atm.services;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.services.SearchFilter;
import org.guce.process.atm.entities.TypeAvtech;
import org.guce.process.atm.repositories.TypeAvtechHistoryRepository;
import org.guce.process.atm.repositories.impl.TypeAvtechRepositoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class TypeAvtechServiceTest {
    protected static List<TypeAvtech> list;

    @Mock
    protected TypeAvtechRepositoryImpl typeAvtechRepository;

    @Mock
    protected TypeAvtechHistoryRepository historyRepository;

    @Mock
    protected IdentifiantGeneratorLocal generator;

    @Mock
    protected CoreChargerFacadeLocal chargerFacade;

    @Mock
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @InjectMocks
    protected TypeAvtechService instance;

    public TypeAvtechServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            TypeAvtech c = new TypeAvtech();
            c.setId(1 + "");
            c.setActive(true);
            c.setLabel(nextString(17));
            c.setCode(nextString(17));
            list.add(c);
        }
    }

    @Before
    public void setUp() {
        Mockito.when(typeAvtechRepository.count()).thenReturn(list.size());
        Mockito.when(typeAvtechRepository.findAll()).thenReturn(list);
        Mockito.when(typeAvtechRepository.find(ArgumentMatchers.any(String.class))).thenReturn(list.get(0));
        Mockito.doAnswer(new Answer<Void>() {
                    @Override
                    public Void answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        TypeAvtech c = (TypeAvtech)args[0];
                        c.setId("1");
                        return null;
                    }
                }).when(typeAvtechRepository).create(ArgumentMatchers.any(TypeAvtech.class));Mockito.when(typeAvtechRepository.search(ArgumentMatchers.any(SearchFilter.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(list);
        Mockito.when(typeAvtechRepository.searchCount(ArgumentMatchers.any(SearchFilter.class))).thenReturn(list.size());
    }

    @Test
    public void testCount() {
        System.out.println("count");
        int expResult = list.size();
        int result = instance.count();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testFindAll_0args() {
        System.out.println("findAll");
        List<TypeAvtech> expResult = list;
        List<TypeAvtech> result = instance.findAll();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testFindBy() {
        System.out.println("findBy");
        String primaryKey = "1";
        TypeAvtech expResult = list.get(0);
        TypeAvtech result = instance.findBy(primaryKey);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testSave_One() {
        System.out.println("save");
        TypeAvtech entity = list.get(0);
        entity.setId(null);
        TypeAvtech expResult = entity;
        TypeAvtech result = instance.save(entity);
        Mockito.verify(typeAvtechRepository, Mockito.times(1)).create(entity);
        Assert.assertEquals(expResult, result);
        result = instance.save(entity);
        Mockito.verify(typeAvtechRepository, Mockito.times(1)).edit(entity);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testRemove_One() {
        System.out.println("remove");
        TypeAvtech entity = list.get(0);
        instance.remove(entity);
        Mockito.verify(typeAvtechRepository, Mockito.times(1)).edit(entity);
    }

    @Test
    public void testRemove_List() {
        System.out.println("remove");
        instance.remove(list);
        Mockito.verify(typeAvtechRepository, Mockito.times(10)).edit(ArgumentMatchers.any(TypeAvtech.class));
    }

    @Test
    public void testSave_List() {
        System.out.println("save");
        instance.save(list);
        Mockito.verify(typeAvtechRepository, Mockito.times(10)).edit(ArgumentMatchers.any(TypeAvtech.class));
    }

    @Test
    public void testSearch() {
        System.out.println("search");
        SearchFilter filter = new SearchFilter();
        int start = 0;
        int count = 0;
        String orderField = "";
        String order = "";
        List<TypeAvtech> expResult = list;
        List<TypeAvtech> result = instance.search(filter, start, count, orderField, order);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testSearchCount() {
        System.out.println("searchCount");
        SearchFilter filter = new SearchFilter();
        int expResult = list.size();
        int result = instance.searchCount(filter);
        Assert.assertEquals(expResult, result);
    }

    public static String nextString(int size) {
        byte[] array = new byte[size];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
}
